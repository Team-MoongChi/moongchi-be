package com.moongchi.moongchi_be.domain.user.entity;


import com.moongchi.moongchi_be.domain.favoriite_product.entity.FavoriteProduct;
import com.moongchi.moongchi_be.domain.chat.entity.Participant;
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

    @Column(nullable = false)
    private String name;

    @Column
    private String nickname;

    @Column
    private String phone;

    @Column
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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupBoard> groupBoards;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<FavoriteProduct> favoriteProducts;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Participant> participants;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private MannerPercent mannerPercent;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

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

    public void updateMannerPercent(MannerPercent mannerPercent){
        this.mannerPercent = mannerPercent;
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

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public User editUser(String nickname, String profileUrl){
        this.nickname = nickname;
        this.profileUrl = profileUrl;
        return this;
    }

}
