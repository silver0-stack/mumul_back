package com.example.ama_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "space")
@Builder
public class SpaceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 스페이스의 고유 id

    private Long userId; // 스페이스 생성한 사용자의 고유 id

    @OneToMany(mappedBy = "space", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<QuestionEntity> questionEntities = new ArrayList<>();


    // 현재 사용자가 소유한 스페이스인지 즉, 본인 스페이스인지 판별하는 메소드
    public boolean isOwnedBy(UserEntity user){
        return user!=null && this.userId!=null && this.userId.equals(user.getId());
    }

}