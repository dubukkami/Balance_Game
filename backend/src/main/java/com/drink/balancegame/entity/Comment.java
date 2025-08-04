package com.drink.balancegame.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
import java.util.ArrayList;
import java.util.List;

/**
 * 댓글 엔티티
 * 밸런스 게임에 대한 사용자의 댓글 정보를 저장
 */
@Entity
@Table(name = "comments",
       indexes = {
           @Index(name = "idx_comments_balance_game", columnList = "balance_game_id"),
           @Index(name = "idx_comments_author", columnList = "author_id"),
           @Index(name = "idx_comments_parent", columnList = "parent_comment_id"),
           @Index(name = "idx_comments_created_at", columnList = "created_at")
       })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    /** 댓글 ID (Primary Key) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** 댓글 내용 */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    /** 부모 댓글 (대댓글인 경우) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    @JsonBackReference("parent-replies")
    private Comment parentComment;
    
    /** 자식 댓글들 (대댓글 목록) */
    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    @JsonManagedReference("parent-replies")
    private List<Comment> replies = new ArrayList<>();
    
    /** 댓글 깊이 (0: 원댓글, 1: 대댓글) */
    @Column(nullable = false)
    @Builder.Default
    private Integer depth = 0;
    
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
    
    /** 댓글 작성자 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    @JsonIgnoreProperties({"balanceGames", "votes", "comments"})
    private User author;
    
    /** 댓글이 속한 밸런스 게임 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "balance_game_id", nullable = false)
    @JsonBackReference("balanceGame-comments")
    private BalanceGame balanceGame;
    
    /** 이 댓글에 대한 좋아요 목록 */
    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    @Builder.Default
    private List<Like> likes = new ArrayList<>();
    
}