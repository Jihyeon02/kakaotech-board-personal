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

    public MembershipController(MembershipService membershipService) {
        this.membershipService = membershipService;
    }

    @PostMapping
    public MembershipDto createMembership(@RequestBody  MembershipReqDto membershipReqDto) {
        return membershipService.save(membershipReqDto);
    }
    @DeleteMapping("/{userId}")
    public Long DeleteMembership(@PathVariable Long userId) {
        return membershipService.delete(userId);
    }

    @PutMapping("/{userId}")
    public MembershipDto UpdateMembership(@PathVariable Long userId, @RequestBody MembershipUpdateReq membershipUpdateReq) {
        MembershipDto tempDto = membershipService.updateNickname(userId, membershipUpdateReq);
        //프로필사진 관련 코드 추가 예정
        return tempDto;
    }

    @GetMapping("/{userId}")
    public MembershipDto getMembership(@PathVariable Long userId) {
        return membershipService.getMembership(userId);
    }

}

