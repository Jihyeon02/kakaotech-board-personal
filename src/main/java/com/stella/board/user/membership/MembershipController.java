package com.stella.board.user.membership;

import com.stella.board.user.User;
import com.stella.board.user.membership.dto.MembershipDto;
import com.stella.board.user.membership.dto.MembershipReqDto;
import com.stella.board.user.membership.dto.MembershipUpdateReq;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/members")
public class MembershipController {
    private final MembershipService membershipService;
    private final User user;

    public MembershipController(MembershipService membershipService, User user) {
        this.membershipService = membershipService;
        this.user = user;
    }

    @PostMapping
    public MembershipDto createMembership(@RequestParam  MembershipReqDto membershipReqDto) {
        membershipService.save(membershipReqDto);
        return MembershipDto.of(user);
    }
    @DeleteMapping("/{userId}")
    public Long DeleteMembership(@PathVariable Long userId) {
        return membershipService.delete(userId);
    }

    @PutMapping("/{userId}")
    public MembershipDto UpdateMembership(@PathVariable Long userId, @RequestParam MembershipUpdateReq membershipUpdateReq) {
        MembershipDto tempDto = membershipService.updateNickname(userId, membershipUpdateReq);
        //프로필사진 관련 코드 추가 예정
        return tempDto;
    }

    @GetMapping
    public MembershipDto getMembership(@PathVariable Long userId) {
        return membershipService.getMembership(userId);
    }

}

