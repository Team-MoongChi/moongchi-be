package com.moongchi.moongchi_be.domain.user.service;

import com.moongchi.moongchi_be.common.auth.jwt.JwtTokenProvider;
import com.moongchi.moongchi_be.common.exception.custom.CustomException;
import com.moongchi.moongchi_be.common.exception.errorcode.ErrorCode;
import com.moongchi.moongchi_be.domain.chat.entity.Participant;
import com.moongchi.moongchi_be.domain.chat.entity.Review;
import com.moongchi.moongchi_be.domain.chat.repository.ParticipantRepository;
import com.moongchi.moongchi_be.domain.chat.repository.ReviewRepository;
import com.moongchi.moongchi_be.domain.user.dto.ReviewKeywordDto;
import com.moongchi.moongchi_be.domain.user.dto.TokenResponseDto;
import com.moongchi.moongchi_be.domain.user.dto.UserBasicDto;
import com.moongchi.moongchi_be.domain.user.dto.UserDto;
import com.moongchi.moongchi_be.domain.user.entity.User;
import com.moongchi.moongchi_be.domain.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ParticipantRepository participantRepository;
    private final ReviewRepository reviewRepository;
    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    public TokenResponseDto createUser(UserDto userDto, HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = authService.getRefreshToken(request);

        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        Long userId = jwtTokenProvider.getUserId(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        User newUser = user.updateUser(userDto.getNickname(), userDto.getPhone(), userDto.getBirth(), userDto.getGender(), userDto.getProfileUrl());
        this.userRepository.save(newUser);

        TokenResponseDto tokenResponseDto = authService.issueToken(request, response);
        return tokenResponseDto;
    }

    public UserBasicDto getUserBasic(HttpServletRequest request){
        String refreshToken = authService.getRefreshToken(request);

        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        Long userId = jwtTokenProvider.getUserId(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        UserBasicDto userBasicDto = UserBasicDto.builder()
                .name(user.getName())
                .email(user.getEmail())
                .build();

        return userBasicDto;
    }

    public User getUser(HttpServletRequest request){
        String token = jwtTokenProvider.resolveToken(request);
        if (token == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        Long userId = jwtTokenProvider.getUserId(token);
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
    }


    public void addLocation(UserDto userDto, HttpServletRequest request){
        User user = getUser(request);
        User newUser = user.updateLocation(userDto.getLatitude(), userDto.getLongitude(), userDto.getAddress());
        this.userRepository.save(newUser);
    }

    public void addInterestCategory(UserDto userDto, HttpServletRequest request){
        User user = getUser(request);
        User newUser = user.updateInterest(userDto.getInterestCategory());
        this.userRepository.save(newUser);
    }

    public void updateUser(UserDto userDto, HttpServletRequest request){
        User user = getUser(request);
        User updateUser = user.editUser(userDto.getNickname(), userDto.getProfileUrl());
        this.userRepository.save(updateUser);
    }

    public ReviewKeywordDto getUserLatestReviewKeywords(Long userId) {
        List<Participant> myParticipants = participantRepository.findByUserId(userId);
        List<Long> participantIds = myParticipants.stream().map(Participant::getId).toList();

        List<Review> reviews = reviewRepository.findTop4ByParticipantIdInOrderByIdDesc(participantIds);

        List<String> keywords = reviews.stream()
                .flatMap(r -> Arrays.stream(
                        Optional.ofNullable(r.getKeywords()).orElse("").split(",")))
                .map(String::trim)                // 혹시 공백 제거
                .filter(kw -> !kw.isBlank())      // 빈 값 제거
                .limit(4)
                .toList();


        return new ReviewKeywordDto(userId, keywords);
    }

}
