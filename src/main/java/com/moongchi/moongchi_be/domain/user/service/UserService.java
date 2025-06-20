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
import com.moongchi.moongchi_be.domain.user.entity.MannerPercent;
import com.moongchi.moongchi_be.domain.user.entity.User;
import com.moongchi.moongchi_be.domain.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ParticipantRepository participantRepository;
    private final ReviewRepository reviewRepository;
    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    public TokenResponseDto createUser(UserDto userDto, String refreshToken) {
        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        Long userId = jwtTokenProvider.getUserId(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        MannerPercent mannerPercent = MannerPercent.builder().leaderPercent(50.0).participantPercent(50.0).build();

        mannerPercent.updateUser(user);
        user.updateMannerPercent(mannerPercent);

        user.updateUser(userDto.getNickname(), userDto.getPhone(), userDto.getBirth(), userDto.getGender(), userDto.getProfileUrl());
        userRepository.save(user);

        TokenResponseDto tokenResponseDto = authService.issueToken(refreshToken);
        return tokenResponseDto;
    }

    public UserBasicDto getUserBasic(String refreshToken) {
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

    public User getUser(HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        if (token == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        Long userId = jwtTokenProvider.getUserId(token);
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
    }


    public void addLocation(UserDto userDto, User user) {
        User newUser = user.updateLocation(userDto.getLatitude(), userDto.getLongitude(), userDto.getAddress());
        this.userRepository.save(newUser);
    }

    public void addInterestCategory(UserDto userDto, User user) {
        User newUser = user.updateInterest(userDto.getInterestCategory());
        this.userRepository.save(newUser);
    }

    public void updateUser(UserDto userDto, User user) {
        User updateUser = user.editUser(userDto.getNickname(), userDto.getProfileUrl());
        this.userRepository.save(updateUser);
    }

    public ReviewKeywordDto getUserLatestReviewKeywords(Long userId) {
        List<Participant> myParticipants = participantRepository.findByUserId(userId);
        List<Long> participantIds = myParticipants.stream().map(Participant::getId).toList();

        List<Review> reviews = reviewRepository.findTop4ByParticipantIdInOrderByIdDesc(participantIds);

        // keywords는 이제 이미 List<String>임!
        List<String> keywords = reviews.stream()
                .flatMap(r -> r.getKeywords().stream())
                .map(String::trim)
                .filter(kw -> !kw.isBlank())
                .limit(4)
                .toList();

        return new ReviewKeywordDto(userId, keywords);
    }

    public UserDto getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        ReviewKeywordDto reviewKeywordDto = getUserLatestReviewKeywords(userId);

        UserDto userDto = UserDto.builder()
                .nickname(user.getNickname())
                .profileUrl(user.getProfileUrl())
                .mannerLeader(user.getMannerPercent().getLeaderPercent())
                .mannerParticipant(user.getMannerPercent().getParticipantPercent())
                .reviewKeywordDto(reviewKeywordDto)
                .build();

        return userDto;
    }
}
