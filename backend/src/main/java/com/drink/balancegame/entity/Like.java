package com.drink.balancegame.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 추천 엔티티
 * 밸런스 게임과 댓글에 대한 사용자의 추천 정보를 저장
 */
@Entity
@Table(name = "likes", 
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"user_id", "balance_game_id"}),
           @UniqueConstraint(columnNames = {"user_id", "comment_id"})
       })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Like {
    /** 추천 ID (Primary Key) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** 추천을 누른 사용자 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"balanceGames", "votes", "comments"})
    private User user;
    
    /** 추천한 밸런스 게임 (게임 추천인 경우) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "balance_game_id")
    @JsonIgnoreProperties({"votes", "comments", "author"})
    private BalanceGame balanceGame;
    
    /** 추천한 댓글 (댓글 추천인 경우) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    @JsonIgnoreProperties({"replies", "author", "balanceGame"})
    private Comment comment;
    
    /** 생성일시 */
    @CreationTimestamp
    private LocalDateTime createdAt;
}