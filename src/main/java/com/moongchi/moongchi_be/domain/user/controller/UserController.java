package com.moongchi.moongchi_be.domain.user.controller;


import com.moongchi.moongchi_be.common.auth.jwt.JwtTokenProvider;
import com.moongchi.moongchi_be.common.util.CookieUtil;
import com.moongchi.moongchi_be.domain.user.dto.ReviewKeywordDto;
import com.moongchi.moongchi_be.domain.user.dto.TokenResponseDto;
import com.moongchi.moongchi_be.domain.user.dto.UserBasicDto;
import com.moongchi.moongchi_be.domain.user.dto.UserDto;
import com.moongchi.moongchi_be.domain.user.entity.User;
import com.moongchi.moongchi_be.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "사용자 정보 입력", description = "로그인 후 추가 정보 입력")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 정보 추가 완료"),
    })
    @PostMapping
    public ResponseEntity<TokenResponseDto> createUSer(@RequestBody UserDto userDto, HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = CookieUtil.getCookieValue(request, "refresh_token");
        TokenResponseDto tokenResponseDto = userService.createUser(userDto, refreshToken);
        return ResponseEntity.ok(tokenResponseDto);
    }

    @Operation(summary = "사용자 정보 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserDto.class))))
    })
    @GetMapping
    public ResponseEntity<UserDto> getUser(HttpServletRequest request) {
        User user = userService.getUser(request);
        UserDto userDto = new UserDto(user);
        return ResponseEntity.ok(userDto);
    }

    @Operation(summary = "사용자 위치 설정", description = "사용자의 위치 설정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "위치 설정 성공")
    })
    @PostMapping("/location")
    public ResponseEntity<?> addLocation(@RequestBody UserDto userDto, HttpServletRequest request) {
        User user = userService.getUser(request);
        userService.addLocation(userDto, user);
        return ResponseEntity.ok("위치 설정이 완료되었습니다.");
    }

    @Operation(summary = "사용자 관심 카테고리 추가")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "관심 카테고리 추가 성공")
    })
    @PostMapping("/interest-category")
    public ResponseEntity<?> addInterestCategory(@RequestBody UserDto userDto, HttpServletRequest request){
        User user = userService.getUser(request);
        userService.addInterestCategory(userDto, user);
        return ResponseEntity.ok("관심 카테고리가 추가되었습니다.");
    }

    @Operation(summary = "사용자 이름 이메일 조회", description = "정보 입력시 필요한 정보 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserBasicDto.class))))
    })
    @GetMapping("/basic")
    public ResponseEntity<UserBasicDto> getUserNameEmail(HttpServletRequest request){
        String refreshToken = CookieUtil.getCookieValue(request, "refresh_token");
        UserBasicDto userBasicDto = userService.getUserBasic(refreshToken);
        return ResponseEntity.ok(userBasicDto);
    }

    @Operation(summary = "회원 정보 수정", description = "회원 닉네임 프로필 사진 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공")
    })
    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody UserDto userDto, HttpServletRequest request){
        User user = userService.getUser(request);
        userService.updateUser(userDto, user);
        return ResponseEntity.ok("회원정보 수정이 완료되었습니다.");
    }

    @Operation(summary = "사용자의 리뷰 키워드 조회", description = "사용자 별 리뷰 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReviewKeywordDto.class))))
    })
    @GetMapping("/reviews")
    public ResponseEntity<ReviewKeywordDto> getReviewKeywords(HttpServletRequest request) {
        ReviewKeywordDto dto = userService.getUserLatestReviewKeywords(request);
        return ResponseEntity.ok(dto);
    }
}
