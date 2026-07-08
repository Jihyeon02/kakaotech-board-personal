package com.stella.board.global.response;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/users")
public class ForTestApiResponse {

    @GetMapping("/email/check")
    public ApiResponse<Boolean> checkEmail(@RequestParam String email) {
        return ApiResponse.success(false); // 항상 false = 중복 아님
    }

    @GetMapping("/nickname/check")
    public ApiResponse<Boolean> checkNickname(@RequestParam String nickname) {
        return ApiResponse.success(false); // 항상 false = 중복 아님
    }

}
