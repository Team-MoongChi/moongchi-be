package com.moongchi.moongchi_be.user.service;

import com.moongchi.moongchi_be.auth.jwt.JwtTokenProvider;
import com.moongchi.moongchi_be.user.dto.TokenResponseDto;
import com.moongchi.moongchi_be.user.dto.UserDto;
import com.moongchi.moongchi_be.user.entity.User;
import com.moongchi.moongchi_be.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
        User newUser = user.updateUser(userDto.getNickname(), userDto.getPhone(), userDto.getBirth(), userDto.getGender());
        this.userRepository.save(newUser);

        TokenResponseDto tokenResponseDto = authService.issueToken(request, response);
        return tokenResponseDto;
    }

    public Optional<User> getUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof UsernamePasswordAuthenticationToken token) {
            Object principal = token.getPrincipal();

            if (principal instanceof UserDetails userDetails) {
                String email = userDetails.getUsername();
                return userRepository.findByEmail(email);
            }

            if (principal instanceof String email) {
                return userRepository.findByEmail(email);
            }
        }

        return Optional.empty();
    }

    public void addLocation(UserDto userDto){
        User user = getUser().get();
        User newUser = user.updateLocation(userDto.getLatitude(), userDto.getLongitude(), userDto.getAddress());
        this.userRepository.save(newUser);
    }

}
