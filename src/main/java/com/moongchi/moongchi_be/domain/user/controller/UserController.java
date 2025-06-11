package com.moongchi.moongchi_be.domain.user.controller;

import com.moongchi.moongchi_be.domain.user.dto.ReviewKeywordDto;
import com.moongchi.moongchi_be.domain.user.dto.TokenResponseDto;
import com.moongchi.moongchi_be.domain.user.dto.UserBasicDto;
import com.moongchi.moongchi_be.domain.user.dto.UserDto;
import com.moongchi.moongchi_be.domain.user.entity.User;
import com.moongchi.moongchi_be.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<TokenResponseDto> createUSer(@RequestBody UserDto userDto, HttpServletRequest request, HttpServletResponse response) {
        TokenResponseDto tokenResponseDto = userService.createUser(userDto, request, response);
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
        userService.addLocation(userDto, request);
        return ResponseEntity.ok("위치 설정이 완료되었습니다.");
    }

    @PostMapping("/interest-category")
    public ResponseEntity<?> addInterestCategory(@RequestBody UserDto userDto, HttpServletRequest request){
        userService.addInterestCategory(userDto, request);
        return ResponseEntity.ok("관심 카테고리가 추가되었습니다.");
    }

    @GetMapping("/basic")
    public ResponseEntity<UserBasicDto> getUserNameEmail(HttpServletRequest request){
        UserBasicDto userBasicDto = userService.getUserBasic(request);
        return ResponseEntity.ok(userBasicDto);
    }

    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody UserDto userDto, HttpServletRequest request){
        userService.updateUser(userDto, request);
        return ResponseEntity.ok("회원정보 수정이 완료되었습니다.");
    }

    @GetMapping("/{userId}/review-keywords")
    public ResponseEntity<ReviewKeywordDto> getReviewKeywords(@PathVariable Long userId) {
        ReviewKeywordDto dto = userService.getUserLatestReviewKeywords(userId);
        return ResponseEntity.ok(dto);
    }


}
