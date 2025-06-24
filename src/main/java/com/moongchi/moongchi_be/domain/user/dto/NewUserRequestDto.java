package com.moongchi.moongchi_be.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.moongchi.moongchi_be.domain.user.enums.Gender;
import lombok.*;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class NewUserRequestDto {

    @JsonProperty("user_id")
    private Long userId;
    private LocalDate birth;
    private Gender gender;
    private String address;
    private String interestCategory;
}
