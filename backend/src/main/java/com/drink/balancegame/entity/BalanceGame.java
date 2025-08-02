package com.drink.balancegame.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 밸런스 게임 엔티티
 * A vs B 선택지로 구성된 밸런스 게임 정보를 저장
 */
@Entity
@Table(name = "balance_games")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceGame {
    /** 밸런스 게임 ID (Primary Key) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** 게임 제목 */
    @Column(nullable = false)
    private String title;
    
    /** 게임 설명 */
    @Column(columnDefinition = "TEXT")
    private String description;
    
    /** 선택지 A */
    @Column(nullable = false)
    private String optionA;
    
    /** 선택지 B */
    @Column(nullable = false)
    private String optionB;
    
    /** 선택지 A 상세 설명 */
    @Column(columnDefinition = "TEXT")
    private String optionADescription;
    
    /** 선택지 B 상세 설명 */
    @Column(columnDefinition = "TEXT")
    private String optionBDescription;
    
    /** 조회수 */
    @Column(nullable = false)
    private Integer viewCount = 0;
    
    
    /** 생성일시 */
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    /** 수정일시 */
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    /** 게임 작성자 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    @JsonIgnoreProperties({"balanceGames", "votes", "comments"})
    private User author;
    
    /** 이 게임에 대한 투표 목록 */
    @OneToMany(mappedBy = "balanceGame", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("balanceGame-votes")
    private List<Vote> votes;
    
    /** 이 게임에 대한 댓글 목록 */
    @OneToMany(mappedBy = "balanceGame", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("balanceGame-comments")
    private List<Comment> comments;
    
    /** 이 게임에 대한 좋아요 목록 */
    @OneToMany(mappedBy = "balanceGame", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("balanceGame-likes")
    private List<Like> likes;
}