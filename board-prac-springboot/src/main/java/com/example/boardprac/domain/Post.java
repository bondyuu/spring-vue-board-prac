package com.example.boardprac.domain;

import com.example.boardprac.dto.PostDto;
import com.example.boardprac.dto.PostRequestDto;
import com.example.boardprac.global.PostStatus;
import com.example.boardprac.service.PostService;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column
    private String title;
    @Column
    private String content;

    @Column
    @Enumerated(EnumType.STRING)
    private PostStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "post", orphanRemoval = true)
    private List<Heart> heartList = new ArrayList<>();

    @CreationTimestamp
    private Timestamp createdAt;
    @UpdateTimestamp
    private Timestamp modifiedAt;

    @Builder
    public Post(String title, String content, User user, PostStatus status) {
        this.title = title;
        this.content = content;
        this.user = user;
        this.status = status;
    }

    public void edit(PostRequestDto requestDto) {
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
    }

    public PostDto toPostDto() {
        return PostDto.builder()
                .id(this.id)
                .title(this.title)
                .content(this.content)
                .user(this.user.toUserDto())
                .heartNum(this.heartList.size())
                .isHeart(checkHeartListContainsUser(this.heartList, this.user))
                .status(this.status)
                .build();
    }

    private boolean checkHeartListContainsUser(List<Heart> list, User user) {
        return list.stream().filter(heart -> heart.getUser().equals(user)).toList().size() > 0;
    }

    public void toBannedPost(PostStatus status) {
        this.status = status;
    }
    public void toDeletedPost(PostStatus status) {
        this.status = status;
    }
}
