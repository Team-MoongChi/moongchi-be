package com.moongchi.moongchi_be.domain.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "manner_percents")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MannerPercent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "manner_percents_id")
    private Long id;

    @Column(name = "leader_percent")
    private Double leaderPercent;

    @Column(name = "participant_percent")
    private Double participantPercent;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public void updateUser(User user) {
        this.user = user;
    }
}
