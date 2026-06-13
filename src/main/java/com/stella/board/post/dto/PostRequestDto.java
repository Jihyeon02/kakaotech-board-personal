package com.stella.board.post.dto;

import com.stella.board.Image;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.util.List;

// 사용자로부터 들어오는 데이터 -> Post / Put의 Body
@Getter
public class PostRequestDto {
    private Long userId;
    private String title;
    private List<Image> images;
    private String content;
    // 사진 관련 필드들 넣어야 함.

    public PostRequestDto(Long userId, String title, List<Image> images, String content) {
        this.userId = userId;
        this.title = title;
        this.images = images;
        this.content = content;
    }
}
