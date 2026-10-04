package com.stella.board.postImage;

public class PostImageUploadException extends RuntimeException {

    public PostImageUploadException(
            String message,
            Throwable cause
    ) {
        super(message, cause);
    }
}