package com.moongchi.moongchi_be.domain.user.dto;

import com.moongchi.moongchi_be.domain.user.entity.User;
import com.moongchi.moongchi_be.domain.user.enums.Gender;
import com.moongchi.moongchi_be.domain.user.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "사용자 DTO")
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
    private Double latitude;
    private Double longitude;
    private String address;
    private UserRole userRole;
    private Double mannerLeader;
    private Double mannerParticipant;
    private ReviewKeywordDto reviewKeywordDto;

    public UserDto(User user) {
        this.id = user.getId();
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.profileUrl = user.getProfileUrl();
        this.address = user.getAddress();
        this.latitude = user.getLatitude();
        this.longitude = user.getLongitude();
        this.mannerLeader = user.getMannerPercent().getLeaderPercent();
        this.mannerParticipant = user.getMannerPercent().getParticipantPercent();
    }
}
