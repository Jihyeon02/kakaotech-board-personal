package com.stella.board.user.membership;

import com.stella.board.user.User;
import com.stella.board.user.UserRepository;
import com.stella.board.user.membership.dto.MembershipDto;
import com.stella.board.user.membership.dto.MembershipReqDto;
import com.stella.board.user.membership.dto.MembershipUpdateReq;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MembershipService {
    private final UserRepository userRepository;

    public MembershipService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Transactional
    // 회원가입 - save
    public MembershipDto save(MembershipReqDto reqDto) {
        User user = new User(null, reqDto.email(), reqDto.password(), reqDto.nickname(),
                reqDto.profile_imageUrl());

        User new_user = userRepository.save(user);

        return MembershipDto.of(new_user);
    }

    @Transactional
    // 회원정보 삭제 - Delete ->추후 소프트 딜리트로 변경할지 고민하고 수정하기
    public Long delete(Long userId) {
        userRepository.deleteById(userId);
        return userId;
    }

    public MembershipDto getMembership(Long userId) {
        userRepository.findById(userId);
        return MembershipDto.of(userRepository.findById(userId).get());
    }

    @Transactional
    // 회원정보 수정 (nickname) - 단건 조회 이후 nickname 수정이 있는데 이걸 하나의 Update 메소드에 적용하나? -> 단건 조회 분리하고 수정한 데이터 옮기는걸 분리하는 게 맞나
    public MembershipDto updateNickname(Long userId, MembershipUpdateReq dto) {
        Optional<User> user = userRepository.findById(userId); // 기존 데이터 단건 조회로 불러와서 엔티티에 담기
        // Dto로 가져온 값 담기 <- 요청으로 받아온 데이터
        String new_nickname = dto.nickname();
        //String new_profile_image_url = dto.profile_image_url();

        // 닉네임 바뀌었는지 검증
        // 바뀌었으면 값 수정해서 Entity에 담기
        if(new_nickname != null && !new_nickname.equals(user.get().getNickname())) {
            MembershipDto membershipDto = new MembershipDto(userId, user.get().getEmail(), user.get().getPassword(), new_nickname, user.get().getProfile_imageUrl());
            User updateUser = new User(userId, membershipDto.email(),membershipDto.password(), membershipDto.nickname(), membershipDto.profile_imageUrl());
            userRepository.save(updateUser);

            return MembershipDto.of(updateUser);
        }
        return null; // 예외처리 추후에 넣어야 함.

    }

    /*@Transactional
    // 회원정보 수정(profile_imageUrl) -> 나중에 DB S3로 교체되면 로직 바뀔 수 있으니 nickname update 메서드랑 분리해둠.
    public MembershipDto updateProfile_imageUrl(Long userId, MembershipUpdateReq dto) {
        Optional<User> user = userRepository.findById(userId); // 기존 데이터 단건 조회로 불러와서 엔티티에 담기
        // Dto로 가져온 값 담기 <- 요청으로 받아온 데이터
        String new_profile_image_url = dto.profile_image_url();

        // url 바뀌었는지 검증 후 바뀌었으면 save -> S3로 DB 바꾸면 이것도 로직 바꿔야함.
        if(new_profile_image_url != null && !new_profile_image_url.equals(user.get().getNickname())) {
            MembershipDto membershipDto = new MembershipDto(userId, user.get().getEmail(), user.get().getPassword(), user.get().getNickname(),user.get().getProfile_imageUrl());
            User updateUser = new User(userId, membershipDto.email(),membershipDto.password(), membershipDto.nickname(), membershipDto.profile_imageUrl());
            userRepository.save(updateUser);

            return MembershipDto.of(updateUser);
        }
        return null; // 예외처리 추후에 넣어야 함.

    }*/

    // 비밀번호 수정 메서드




}
