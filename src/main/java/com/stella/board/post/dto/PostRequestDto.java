package com.stella.board.post.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PostRequestDto(

        @NotNull(message = "사용자 ID는 필수입니다.")
        Long userId,

        @NotBlank(message = "제목은 필수입니다.")
        @Size(max = 200, message = "제목은 200자를 초과할 수 없습니다.")
        String title,

        @Size(max = 500, message = "요약은 500자를 초과할 수 없습니다.")
        String summary,

        @NotBlank(message = "본문은 필수입니다.")
        String content
) {
}