package com.stella.board.postImage;

public class PostImageNotFoundException
        extends RuntimeException {

    public PostImageNotFoundException(
            Long postId,
            Long imageId
    ) {
        super(
                "이미지를 찾을 수 없습니다. postId="
                        + postId
                        + ", imageId="
                        + imageId
        );
    }
}