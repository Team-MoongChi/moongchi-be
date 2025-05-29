package com.moongchi.moongchi_be.domain.user.dto;

import com.moongchi.moongchi_be.domain.user.entity.User;
import com.moongchi.moongchi_be.domain.user.enums.Gender;
import com.moongchi.moongchi_be.domain.user.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UserDto {
    private Long id;
    private String name;
    private String nickname;
    private String phone;
    private String email;
    private String profileUrl;
    private LocalDate birth;
    private Gender gender;
    private String interestCategory;
    private double latitude;
    private double longitude;
    private String address;
    private UserRole userRole;
    private double mannerLeader;
    private double mannerParticipant;

    public UserDto(User user) {
        this.id = user.getId();
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.profileUrl = user.getProfileUrl();
        this.address = user.getAddress();
        this.latitude = user.getLatitude();
        this.longitude = user.getLongitude();
        this.mannerLeader = user.getMannerLeader();
        this.mannerParticipant = user.getMannerParticipant();
    }
}
