package com.stella.board.postImage;

import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

@Component
public class S3TransactionCompensator {

    private final S3ImageStorage s3ImageStorage;

    public S3TransactionCompensator(
            S3ImageStorage s3ImageStorage
    ) {
        this.s3ImageStorage = s3ImageStorage;
    }

    public void deleteAllOnRollback(List<String> imageKeys) {
        List<String> keys = List.copyOf(imageKeys);

        assertTransactionActive();

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCompletion(int status) {
                        if (status != STATUS_COMMITTED) {
                            keys.forEach(
                                    s3ImageStorage::deleteQuietly
                            );
                        }
                    }
                }
        );
    }

    public void deleteAfterCommit(String imageKey) {
        assertTransactionActive();

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        s3ImageStorage.deleteQuietly(imageKey);
                    }
                }
        );
    }

    private void assertTransactionActive() {
        if (!TransactionSynchronizationManager
                .isSynchronizationActive()) {
            throw new IllegalStateException(
                    "활성화된 DB 트랜잭션이 없습니다."
            );
        }
    }
}