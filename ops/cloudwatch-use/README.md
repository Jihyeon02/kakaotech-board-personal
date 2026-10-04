# Sysbench USE monitoring

Region: `ap-northeast-2`

- DB EC2: `i-0d2e3e1fab69e7046`
- Load generator EC2: `i-0ae73fa4e069816b4`
- Custom metric namespace: `Sysbench/USE`
- Resolution: 60 seconds

The agent configuration deliberately publishes only the `InstanceId` dimension.
Do not add container IDs, query text, table names, thread IDs, or benchmark run IDs
as dimensions; each unique dimension set is a separately billed metric series.

## 1. Apply the CloudWatch Agent configuration

On the DB instance, install `cloudwatch-agent-db.json` as:

```text
/opt/aws/amazon-cloudwatch-agent/etc/amazon-cloudwatch-agent.json
```

On the load generator, install `cloudwatch-agent-loadgen.json` at the same path.

Before replacing the current file on either instance, keep a backup:

```bash
sudo cp /opt/aws/amazon-cloudwatch-agent/etc/amazon-cloudwatch-agent.json \
  /opt/aws/amazon-cloudwatch-agent/etc/amazon-cloudwatch-agent.json.before-use
```

After saving the appropriate file on each instance:

```bash
sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl \
  -a fetch-config -m ec2 -s \
  -c file:/opt/aws/amazon-cloudwatch-agent/etc/amazon-cloudwatch-agent.json

sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl \
  -a status -m ec2
```

## 2. Install the host sampler on both instances

Copy the files to these paths:

```bash
sudo install -m 0755 cw-host-stats.sh /usr/local/bin/cw-host-stats.sh
sudo install -m 0644 cw-host-stats.service /etc/systemd/system/cw-host-stats.service
sudo install -m 0644 cw-host-stats.timer /etc/systemd/system/cw-host-stats.timer

sudo systemctl daemon-reload
sudo systemctl enable --now cw-host-stats.timer
sudo systemctl start cw-host-stats.service
```

The sampler publishes `system_load1`. It publishes swap-in and swap-out only when
the instance actually has swap enabled. A blank swap graph on a no-swap EC2
instance is expected and does not indicate a failure.

## 3. Set up the MySQL sampler on the DB instance

Find the MySQL container name:

```bash
docker ps --format 'table {{.Names}}\t{{.Image}}\t{{.Status}}'
```

Open a MySQL root session in that container:

```bash
docker exec -it YOUR_MYSQL_CONTAINER mysql -uroot -p
```

Create a read-only monitoring account. Replace the password before running this:

```sql
CREATE USER IF NOT EXISTS 'cwmonitor'@'localhost'
  IDENTIFIED BY 'REPLACE_WITH_A_RANDOM_PASSWORD';
ALTER USER 'cwmonitor'@'localhost'
  IDENTIFIED BY 'REPLACE_WITH_A_RANDOM_PASSWORD';
GRANT PROCESS ON *.* TO 'cwmonitor'@'localhost';
FLUSH PRIVILEGES;
```

Create the private configuration directory first:

```bash
sudo install -d -m 0755 /etc/sysbench-monitor
```

Then create `/etc/sysbench-monitor/mysql.env` on the DB host. Do not commit this file:

```text
MYSQL_CONTAINER=YOUR_MYSQL_CONTAINER
MYSQL_USER=cwmonitor
MYSQL_PASSWORD=REPLACE_WITH_THE_SAME_PASSWORD
```

`CREATE USER IF NOT EXISTS` does not change the password of an account that
already exists. The `ALTER USER` statement above deliberately makes the setup
idempotent and ensures that the password matches `mysql.env`. For the initial
test, use a password containing only letters and digits so that shell/systemd
quoting cannot hide an unrelated configuration mistake.

Verify the exact authentication path used by the service without printing the
password:

```bash
set -a
source /etc/sysbench-monitor/mysql.env
set +a
MYSQL_PWD="$MYSQL_PASSWORD" docker exec --env MYSQL_PWD "$MYSQL_CONTAINER" \
  mysql --batch --skip-column-names --user="$MYSQL_USER" \
  --execute='SELECT CURRENT_USER(), 1;'
unset MYSQL_PASSWORD MYSQL_PWD
```

The expected result starts with `cwmonitor@localhost` and ends with `1`.

Install and start the collector:

```bash
sudo chmod 0600 /etc/sysbench-monitor/mysql.env
sudo install -m 0755 cw-mysql-stats.sh /usr/local/bin/cw-mysql-stats.sh
sudo install -m 0644 cw-mysql-stats.service /etc/systemd/system/cw-mysql-stats.service
sudo install -m 0644 cw-mysql-stats.timer /etc/systemd/system/cw-mysql-stats.timer

sudo systemctl daemon-reload
sudo systemctl enable --now cw-mysql-stats.timer
sudo systemctl start cw-mysql-stats.service
```

## 4. Verify before creating the dashboard

On both instances:

```bash
sudo systemctl status amazon-cloudwatch-agent --no-pager
sudo systemctl status cw-host-stats.timer --no-pager
sudo journalctl -u cw-host-stats.service -n 20 --no-pager
```

Additionally on the DB instance:

```bash
sudo systemctl status cw-mysql-stats.timer --no-pager
sudo journalctl -u cw-mysql-stats.service -n 30 --no-pager
```

If the MySQL service exits successfully, wait two to three minutes and check the
`Sysbench/USE` namespace. `RATE()`-based widgets need at least two samples, so
they can remain blank for the first one or two minutes.

## 5. Create the dashboard

Either paste `cloudwatch-dashboard.json` into **CloudWatch > Dashboards >
Actions > View/edit source**, or run:

```bash
aws cloudwatch put-dashboard \
  --region ap-northeast-2 \
  --dashboard-name Sysbench-USE \
  --dashboard-body file://cloudwatch-dashboard.json
```

The dashboard has a `DB EBS Volume` selector. Select the EBS volume that stores
Docker/MySQL data. To list the volumes attached to the DB instance:

```bash
aws ec2 describe-instances \
  --region ap-northeast-2 \
  --instance-ids i-0d2e3e1fab69e7046 \
  --query 'Reservations[0].Instances[0].BlockDeviceMappings[].{Device:DeviceName,Volume:Ebs.VolumeId}' \
  --output table
```

If MySQL uses Docker's default `/var/lib/docker` on the root filesystem, select
the root EBS volume. On a non-Nitro instance, the three EBS limit/error check
metrics can remain blank because those metrics are Nitro-only.

## 6. Repeatable Sysbench stress run

Copy `sysbench-stress.conf.example` to `~/sysbench-stress.conf` on the load
generator, replace the database address, user, and password, then protect it:

```bash
cp sysbench-stress.conf.example ~/sysbench-stress.conf
chmod 0600 ~/sysbench-stress.conf
install -m 0755 run-sysbench-stress.sh ~/run-sysbench-stress.sh
```

After recreating the `sbtest` database, prepare the fixed dataset once:

```bash
DB_INSTANCE_TYPE=m7i.large LOADGEN_INSTANCE_TYPE=c7i.large \
  ~/run-sysbench-stress.sh prepare
```

Run the thread ramp. Keep all timing and dataset variables identical for every
EC2 instance type:

```bash
DB_INSTANCE_TYPE=m7i.large \
LOADGEN_INSTANCE_TYPE=c7i.large \
THREADS_LIST='1 2 4 8 16 32 64 128' \
WARMUP_SECONDS=120 \
RUN_SECONDS=300 \
COOLDOWN_SECONDS=120 \
  ~/run-sysbench-stress.sh run
```

Results are written below `~/sysbench-results`. Each run contains raw Sysbench
logs, sanitized environment metadata, and `cloudwatch-windows.csv` with UTC and
KST measurement windows for exact CloudWatch dashboard selection.
