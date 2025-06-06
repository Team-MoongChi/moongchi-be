package com.moongchi.moongchi_be.domain.user.entity;

import com.moongchi.moongchi_be.domain.group_boards.entity.GroupBoard;
import com.moongchi.moongchi_be.domain.user.enums.Gender;
import com.moongchi.moongchi_be.domain.user.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
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

    @Column(name = "interest_category")
    private String interestCategory;

    @Column(name = "user_role")
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Column(name = "manner_leader")
    private double mannerLeader;

    @Column(name = "manner_participant")
    private double mannerParticipant;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupBoard> groupBoards;

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

    public User updateUser(String nickname, String phone, LocalDate birth, Gender gender, String profileUrl) {
        this.nickname = nickname;
        this.phone = phone;
        this.birth = birth;
        this.gender = gender;
        this.profileUrl = profileUrl;

        return this;
    }

    public User updateLocation(double latitude, double longitude, String address) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;

        return this;
    }

    public User updateInterest(String interestCategory) {
        this.interestCategory = interestCategory;
        return this;
    }

}
