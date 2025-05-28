package com.moongchi.moongchi_be.user;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
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

    @Column(name = "manner_leader")
    private double mannerLeader;

    @Column(name = "manner_participant")
    private double mannerParticipant;

    @Column(name = "interest-product")
    private String interestProduct;

    @CreationTimestamp
    @Column(name = "create_at")
    private LocalDateTime createAt;

    @UpdateTimestamp
    @Column(name = "update_at")
    private LocalDateTime updateAt;
}
