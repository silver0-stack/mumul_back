package com.example.ama_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "questions")
public class QuestionEntity {
    @Id
    @GeneratedValue(generator ="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    private String id; // 이 오브젝트의 아이디
    private String nickName;  // 익명 유저의 닉네임
    private String questionText; // 질문 내용
    private LocalDateTime createdTime;  // 질문이 올라온 시간
    // @OneToMany 어노테이션을 사용하여 AnswerEntity 클래스의 question 프로퍼티와 매핑하겠습니다.
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private List<AnswerEntity> answers = new ArrayList<>(); //종속된 답변 리스트

}
