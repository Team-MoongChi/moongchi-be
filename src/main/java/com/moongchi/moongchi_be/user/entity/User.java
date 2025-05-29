package com.moongchi.moongchi_be.user.entity;

import com.moongchi.moongchi_be.user.enums.Gender;
import com.moongchi.moongchi_be.user.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String nickname;

    @Column
    private String phone;

    @Column(unique = true)
    private String email;

    @Column(name = "profile_url")
    private String profileUrl;

    @Column
    private LocalDate birth;

    @Column
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column
    private double latitude;

    @Column
    private double longitude;

    @Column
    private String address;

    @Column
    private String provider;

    @Column(name = "user_role")
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Column(name = "manner_leader")
    private double mannerLeader;

    @Column(name = "manner_participant")
    private double mannerParticipant;

    @CreationTimestamp
    @Column(name = "create_at")
    private LocalDateTime createAt;

    @UpdateTimestamp
    @Column(name = "update_at")
    private LocalDateTime updateAt;

    public User update(String name) {
        this.name = name;
        return this;
    }

    public User updateUser(String nickname, String phone, LocalDate birth, Gender gender){
        this.nickname = nickname;
        this.phone = phone;
        this.birth = birth;
        this.gender = gender;

        return this;
    }

    public User updateLocation(double latitude, double longitude, String address){
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;

        return this;
    }


}
