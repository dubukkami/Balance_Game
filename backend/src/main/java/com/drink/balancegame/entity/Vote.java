package com.drink.balancegame.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 투표 엔티티
 * 사용자가 밸런스 게임에 대해 선택한 옵션을 저장
 * 한 사용자는 한 게임에 대해 한 번만 투표 가능
 */
@Entity
@Table(name = "votes", 
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"user_id", "balance_game_id"})
       },
       indexes = {
           @Index(name = "idx_votes_balance_game", columnList = "balance_game_id"),
           @Index(name = "idx_votes_user", columnList = "user_id"),
           @Index(name = "idx_votes_created_at", columnList = "created_at"),
           @Index(name = "idx_votes_option", columnList = "selected_option")
       })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vote {
    /** 투표 ID (Primary Key) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** 선택된 옵션 (A 또는 B) */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoteOption selectedOption;
    
    /** 투표 일시 */
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    /** 투표한 사용자 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"balanceGames", "votes", "comments"})
    private User user;
    
    /** 투표된 밸런스 게임 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "balance_game_id", nullable = false)
    @JsonBackReference("balanceGame-votes")
    private BalanceGame balanceGame;
    
    /** 투표 옵션 열거형 */
    public enum VoteOption {
        A, B
    }
}