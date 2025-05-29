package com.moongchi.moongchi_be.domain.user.service;

import com.moongchi.moongchi_be.common.auth.jwt.JwtTokenProvider;
import com.moongchi.moongchi_be.domain.user.dto.TokenResponseDto;
import com.moongchi.moongchi_be.domain.user.dto.UserDto;
import com.moongchi.moongchi_be.domain.user.entity.User;
import com.moongchi.moongchi_be.domain.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    public TokenResponseDto createUser(UserDto userDto, HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = authService.getRefreshToken(request);

        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }

        String email = jwtTokenProvider.getUserPk(refreshToken);
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (!optionalUser.isPresent()) {
            throw new IllegalArgumentException("유저를 찾을 수 없습니다.");
        }

        User user = optionalUser.get();
        User newUser = user.updateUser(userDto.getNickname(), userDto.getPhone(), userDto.getBirth(), userDto.getGender(), userDto.getProfileUrl());
        this.userRepository.save(newUser);

        TokenResponseDto tokenResponseDto = authService.issueToken(request, response);
        return tokenResponseDto;
    }

    public Optional<User> getUser(HttpServletRequest request){
        String token = jwtTokenProvider.resolveToken(request);
        if (token == null) return Optional.empty();

        Long userId = jwtTokenProvider.getUserId(token);
        return userRepository.findById(userId);

    }

    public void addLocation(UserDto userDto, HttpServletRequest request){
        User user = getUser(request).get();
        User newUser = user.updateLocation(userDto.getLatitude(), userDto.getLongitude(), userDto.getAddress());
        this.userRepository.save(newUser);
    }

    public void addInterestCategory(UserDto userDto, HttpServletRequest request){
        User user = getUser(request).get();
        User newUser = user.updateInterest(userDto.getInterestCategory());
        this.userRepository.save(newUser);
    }

}
