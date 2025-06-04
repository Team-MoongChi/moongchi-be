package com.moongchi.moongchi_be.domain.user.service;

import com.moongchi.moongchi_be.common.auth.jwt.JwtTokenProvider;
import com.moongchi.moongchi_be.common.exception.custom.CustomException;
import com.moongchi.moongchi_be.common.exception.errorcode.ErrorCode;
import com.moongchi.moongchi_be.domain.user.dto.TokenResponseDto;
import com.moongchi.moongchi_be.domain.user.dto.UserBasicDto;
import com.moongchi.moongchi_be.domain.user.dto.UserDto;
import com.moongchi.moongchi_be.domain.user.entity.User;
import com.moongchi.moongchi_be.domain.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
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

}
