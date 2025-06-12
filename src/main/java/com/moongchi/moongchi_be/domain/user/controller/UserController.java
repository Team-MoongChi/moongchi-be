package com.moongchi.moongchi_be.domain.user.controller;


import com.moongchi.moongchi_be.common.auth.jwt.JwtTokenProvider;
import com.moongchi.moongchi_be.common.util.CookieUtil;
import com.moongchi.moongchi_be.domain.user.dto.ReviewKeywordDto;
import com.moongchi.moongchi_be.domain.user.dto.TokenResponseDto;
import com.moongchi.moongchi_be.domain.user.dto.UserBasicDto;
import com.moongchi.moongchi_be.domain.user.dto.UserDto;
import com.moongchi.moongchi_be.domain.user.entity.User;
import com.moongchi.moongchi_be.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "유저", description = "유저 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping
    public ResponseEntity<TokenResponseDto> createUSer(@RequestBody UserDto userDto, HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = CookieUtil.getCookieValue(request, "refresh_token");
        TokenResponseDto tokenResponseDto = userService.createUser(userDto, refreshToken);
        return ResponseEntity.ok(tokenResponseDto);
    }

    @GetMapping
    public ResponseEntity<UserDto> getUser(HttpServletRequest request) {
        User user = userService.getUser(request);
        UserDto userDto = new UserDto(user);
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/location")
    public ResponseEntity<?> addLocation(@RequestBody UserDto userDto, HttpServletRequest request) {
        User user = userService.getUser(request);
        userService.addLocation(userDto, user);
        return ResponseEntity.ok("위치 설정이 완료되었습니다.");
    }

    @PostMapping("/interest-category")
    public ResponseEntity<?> addInterestCategory(@RequestBody UserDto userDto, HttpServletRequest request){
        User user = userService.getUser(request);
        userService.addInterestCategory(userDto, user);
        return ResponseEntity.ok("관심 카테고리가 추가되었습니다.");
    }

    @GetMapping("/basic")
    public ResponseEntity<UserBasicDto> getUserNameEmail(HttpServletRequest request){
        String refreshToken = CookieUtil.getCookieValue(request, "refresh_token");
        UserBasicDto userBasicDto = userService.getUserBasic(refreshToken);
        return ResponseEntity.ok(userBasicDto);
    }

    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody UserDto userDto, HttpServletRequest request){
        User user = userService.getUser(request);
        userService.updateUser(userDto, user);
        return ResponseEntity.ok("회원정보 수정이 완료되었습니다.");
    }

    @GetMapping("/reviews")
    public ResponseEntity<ReviewKeywordDto> getReviewKeywords(HttpServletRequest request) {
        ReviewKeywordDto dto = userService.getUserLatestReviewKeywords(request);
        return ResponseEntity.ok(dto);
    }

}
