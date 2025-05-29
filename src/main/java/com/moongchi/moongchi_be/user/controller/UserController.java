package com.moongchi.moongchi_be.user.controller;

import com.moongchi.moongchi_be.user.dto.TokenResponseDto;
import com.moongchi.moongchi_be.user.dto.UserDto;
import com.moongchi.moongchi_be.user.entity.User;
import com.moongchi.moongchi_be.user.service.UserService;
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
    public ResponseEntity<UserDto> getUser() {
        User user = userService.getUser().get();
        UserDto userDto = new UserDto(user);
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/location")
    public ResponseEntity<?> addLocation(@RequestBody UserDto userDto) {
        userService.addLocation(userDto);
        return ResponseEntity.ok().build();
    }

}
