package com.stella.board.postImage;

public class InvalidPostImageException
        extends RuntimeException {

    public InvalidPostImageException(String message) {
        super(message);
    }
}