package com.stella.board.post.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

// 사용자로부터 들어오는 데이터 -> Post / Put의 Body
@Getter
public class PostRequestDto {
    private Long userId;
    private String title;
    private String content;
    // 사진 관련 필드들 넣어야 함.

    public PostRequestDto(Long userId, String title, String content) {
        this.userId = userId;
        this.title = title;
        this.content = content;
    }
}
