package com.stella.board.user.membership.dto;

import com.stella.board.user.User;

public record MembershipDto(Long userId, String email, String password, String nickname, String profile_imageUrl) {
    public static MembershipDto of(User user) {
        return new MembershipDto(user.getUser_id(), user.getEmail(), user.getPassword(), user.getNickname(), user.getProfile_imageUrl());
    }

}