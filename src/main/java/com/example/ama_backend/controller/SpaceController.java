package com.example.ama_backend.controller;


import com.example.ama_backend.dto.AnswerDTO;
import com.example.ama_backend.dto.QuestionDTO;
import com.example.ama_backend.dto.ResponseDTO;
import com.example.ama_backend.entity.*;
import com.example.ama_backend.persistence.*;
import com.example.ama_backend.service.QAService;
import com.example.ama_backend.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

import static com.example.ama_backend.dto.UserUpdateRequestDto.convertToDto;

@Controller
@RequestMapping("/spaces")
public class SpaceController {
    @Autowired
    private QAService qaService;
    @Autowired
    private SpaceRepository spaceRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private HttpSession httpSession;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private AnswerRepository answerRepository;


    // 내가 보낸 질문 조회
    public ResponseEntity<ResponseDTO<QuestionDTO>> getMySentQuestions(Long spaceId) {
        try {
            // 해당 스페이스의 주인 유저의 고유 아이디 가져오기
            SpaceEntity space = spaceRepository.findById(spaceId).orElseThrow(() -> new IllegalArgumentException("Invalid space id"));
            Long ownerUserId = space.getUserId();

            List<QuestionEntity> questionEntities = questionRepository.findBySendingUserId(ownerUserId);
            List<QuestionDTO> questionDTOs = questionEntities.stream().map(QuestionDTO::new).collect(Collectors.toList());
            ResponseDTO<QuestionDTO> responseDTO = ResponseDTO.<QuestionDTO>builder().data(questionDTOs).build();

            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            String err = e.getMessage();
            ResponseDTO<QuestionDTO> responseDTO = ResponseDTO.<QuestionDTO>builder().error(err).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    //내가 받은 질문 조회
    public ResponseEntity<ResponseDTO<QuestionDTO>> getMyReceivedQuestions(Long spaceId) {
        try {
            // 해당 스페이스의 주인 유저의 고유 아이디 가져오기
            SpaceEntity space = spaceRepository.findById(spaceId).orElseThrow(() -> new IllegalArgumentException("Invalid space id"));
            Long ownerUserId = space.getUserId();

            List<QuestionEntity> questionEntities = questionRepository.findByReceivingUserId(ownerUserId);
            List<QuestionDTO> questionDTOs = questionEntities.stream().map(QuestionDTO::new).collect(Collectors.toList());
            ResponseDTO<QuestionDTO> responseDTO = ResponseDTO.<QuestionDTO>builder().data(questionDTOs).build();

            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            String err = e.getMessage();
            ResponseDTO<QuestionDTO> responseDTO = ResponseDTO.<QuestionDTO>builder().error(err).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // 내가 보낸 질문에 대한 답변 조회
    public ResponseEntity<ResponseDTO<AnswerDTO>> getMySentAnswer(Long spaceId) {
        try {
            // 해당 스페이스의 주인 유저의 고유 아이디 가져오기
            SpaceEntity space = spaceRepository.findById(spaceId).orElseThrow(() -> new IllegalArgumentException("Invalid space id"));
            Long ownerUserId = space.getUserId();

            List<QuestionEntity> questionEntities = questionRepository.findBySendingUserId(ownerUserId);

            List<AnswerDTO> answerDTOS = new ArrayList<>();

            // 각 질문에 대한 답변 리스트 가져오기
            for (QuestionEntity question : questionEntities) {
                List<AnswerEntity> answerEntities = question.getAnswers();
                List<AnswerDTO> answers = answerEntities.stream().map(AnswerDTO::new).toList();
                answerDTOS.addAll(answers);
            }

            ResponseDTO<AnswerDTO> responseDTO = ResponseDTO.<AnswerDTO>builder().data(answerDTOS).build();

            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            String err = e.getMessage();
            ResponseDTO<AnswerDTO> responseDTO = ResponseDTO.<AnswerDTO>builder().error(err).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // 내가 받은 질문에 대한 답변 조회
    public ResponseEntity<ResponseDTO<AnswerDTO>> getMyReceivedAnswer(Long spaceId) {
        try {
            // 해당 스페이스의 주인 유저의 고유 아이디 가져오기
            SpaceEntity space = spaceRepository.findById(spaceId).orElseThrow(() -> new IllegalArgumentException("Invalid space id"));
            Long ownerUserId = space.getUserId();

            List<QuestionEntity> questionEntities = questionRepository.findByReceivingUserId(ownerUserId);

            List<AnswerDTO> answerDTOS = new ArrayList<>();

            // 각 질문에 대한 답변 리스트 가져오기
            for (QuestionEntity question : questionEntities) {
                List<AnswerEntity> answerEntities = question.getAnswers();
                List<AnswerDTO> answers = answerEntities.stream().map(AnswerDTO::new).toList();
                answerDTOS.addAll(answers);
            }

            ResponseDTO<AnswerDTO> responseDTO = ResponseDTO.<AnswerDTO>builder().data(answerDTOS).build();

            return ResponseEntity.ok().body(responseDTO);

        } catch (Exception e) {
            String err = e.getMessage();
            ResponseDTO<AnswerDTO> responseDTO = ResponseDTO.<AnswerDTO>builder().error(err).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }


    @GetMapping("/{spaceId}")
    public ResponseEntity getSpaceInfo(@PathVariable Long spaceId) throws Exception {
        //이동한 스페이스 엔터티
        SpaceEntity space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid space id"));
        // 이동한 스페이스의 주인유저 엔터티
        UserEntity ownerUser = userRepository.findById(space.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid user id"));

        // 현재 로그인한 유저
        org.springframework.security.core.Authentication testAuthentication = SecurityContextHolder.getContext().getAuthentication();
        if (testAuthentication != null) {
            // 현재 로그인한 유저 아이디
            long currentUserId = Long.parseLong((String) testAuthentication.getPrincipal());

            UserEntity user = userService.getUser(ownerUser.getId());
            System.out.println("owneruser.getid: " + ownerUser.getId());
            System.out.println("currentuserid:" + currentUserId);
            return ResponseEntity.ok().body(convertToDto(user));
        } else {
            return ResponseEntity.ok().body(convertToDto(ownerUser));
        }
    }

    // 답변 등록 API
    @PostMapping("/{spaceId}/{questionId}/answer/create")
    public ResponseEntity<?> createAnswer(@PathVariable Long questionId, @PathVariable Long spaceId, @RequestBody AnswerDTO answerDTO) {

        try {

            // 답변달 질문 엔터티
            QuestionEntity question = questionRepository.findById(questionId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid question id"));

            // AnswerEntity 로 변환
            AnswerEntity answerEntity = AnswerDTO.toEntity(answerDTO);


            // id를 null 로 초기화한다. 생성 당시에는 id가 없어야 하기 때문이다.
            answerEntity.setId(null);

            // 서비스를 이용해 질문 엔티티를 생성한다
            List<AnswerEntity> entities = qaService.saveAnswer(answerEntity);

            question.setAnswers(entities);


            // 자바 스트림을 이요해 리턴된 엔티티 리스트를  QuestionDTO 로 변환한다.
            List<AnswerDTO> dtos = entities.stream().map(AnswerDTO::new).collect(Collectors.toList());

            // 변환된 QuestionDTO 리스트를 이용해 ResponseDTO 를 초기화한다.
            ResponseDTO<AnswerDTO> responseDTO = ResponseDTO.<AnswerDTO>builder().data(dtos).build();

            // ResponseDTO 를 리턴한다.
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            // 혹시 예외가 있으면 dto 대신 error 에 메시지를 넣어 리턴한다
            String err = e.getMessage();
            e.printStackTrace();
            ResponseDTO<AnswerDTO> responseDTO = ResponseDTO.<AnswerDTO>builder().error(err).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // 내가 보낸 답변 삭제 API
    // 내 스페이스여야 함
    // 내가 보낸 답변이여야 함
    // Todo - 내가 보낸 답변만 삭제 가능해야 하는데 지금은 내 스페이스기만 하면 모든 답변 삭제 가능함
    @DeleteMapping("{spaceId}/{answerId}/answer/delete")
    public ResponseEntity<?> deleteAnswer(@PathVariable Long answerId, @PathVariable Long spaceId) {
        try {
            // 이동한 스페이스 엔터티
            SpaceEntity space = spaceRepository.findById(spaceId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid space id"));

            // 현재 로그인한 유저
            org.springframework.security.core.Authentication testAuthentication = SecurityContextHolder.getContext().getAuthentication();
            // 현재 로그인한 세션유저로 찾은 현재 유저 엔터티
            long currentUserId = Long.parseLong((String) testAuthentication.getPrincipal());
            UserEntity user = userService.getUser(currentUserId);

            // 현재 스페이스가 내 스페이스라면
            if (space.isOwnedBy(user)) {
                // 서비스를 이용해 답변 엔티티를 삭제한다
                assert user != null;
                qaService.deleteAnswer(answerId, user.getId());
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.badRequest().body("내 스페이스가 아니어서 삭제 불가능합니다.");
            }
        } catch (Exception e) {
            // 혹시 예외가 있으면 dto 대신 error 에 메시지를 넣어 리턴한다
            String err = e.getMessage();
            ResponseDTO<AnswerDTO> responseDTO = ResponseDTO.<AnswerDTO>builder().error(err).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }


    @GetMapping("/{spaceId}/received/get")
    public ResponseEntity<ResponseDTO<QuestionDTO>> getReceivedQuestion(@PathVariable Long spaceId) {
        // 이동한 스페이스 엔터티
        SpaceEntity space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid space id"));

        // 이동한 스페이스 주인아이디로 유저엔터티 찾기 -- 질문 받는 스페이스 주인 유저
        UserEntity spaceUser = userRepository.findById(space.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid user id"));

        try {
            // QuestionEntity 로 변환
            List<QuestionEntity> questionList = qaService.getMyReceivingQuestions(spaceUser.getId());

            // 자바 스트림을 이용해 리턴된 엔티티 리스트를 QuestionDTO 로 변환한다.
            List<QuestionDTO> dtos = questionList.stream().map(QuestionDTO::new).collect(Collectors.toList());

            // 변환된 QuestionDTO 리스트를 이용해 ResponseDTO 를 초기화한다.
            ResponseDTO<QuestionDTO> responseDTO = ResponseDTO.<QuestionDTO>builder().data(dtos).build();

            // ResponseDTO 를 리턴한다.
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            // 혹시 예외가 있으면 dto 대신 error에 메시지를 넣어 리턴한다
            String err = e.getMessage();
            ResponseDTO<QuestionDTO> responseDTO = ResponseDTO.<QuestionDTO>builder().error(err).build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);
        }
    }


    // 보낸 질문과 답변 조회 api
    @GetMapping("/{spaceId}/sent/get")
    public ResponseEntity<?> getSentQuestion(@PathVariable Long spaceId) {

        // 이동한 스페이스 엔터티
        SpaceEntity space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid space id"));

        // 이동한 스페이스 주인아이디로 유저엔터티 찾기 -- 질문 받는 스페이스 주인 유저
        UserEntity spaceUser = userRepository.findById(space.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid user id"));


        try {

            // QuestionEntity 로 변환
            List<QuestionEntity> questionList =qaService.getMySendingQuestions(spaceUser.getId());

            // 자바 스트림을 이요해 리턴된 엔티티 리스트를  QuestionDTO 로 변환한다.
            List<QuestionDTO> dtos = questionList.stream().map(QuestionDTO::new).collect(Collectors.toList());

            // 변환된 QuestionDTO 리스트를 이용해 ResponseDTO 를 초기화한다.
            ResponseDTO<QuestionDTO> responseDTO = ResponseDTO.<QuestionDTO>builder().data(dtos).build();

            // ResponseDTO 를 리턴한다.
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            // 혹시 예외가 있으면 dto 대신 error에 메시지를 넣어 리턴한다
            String err = e.getMessage();
            ResponseDTO<QuestionDTO> responseDTO = ResponseDTO.<QuestionDTO>builder().error(err).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }


    }


    // 질문 등록 API
    @PostMapping("/{spaceId}/question/create")
    public ResponseEntity<?> createQuestion(@PathVariable Long spaceId, @RequestBody QuestionDTO questionDTO) {

        // 이동한 스페이스 엔터티
        SpaceEntity space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid space id"));

        // 이동한 스페이스 주인아이디로 유저엔터티 찾기 -- 질문 받는 스페이스 주인 유저
        UserEntity spaceUser = userRepository.findById(space.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid user id"));

        // 현 로그인한 유저
        //TODO : 로그아웃 후에 다른 계정으로 재로그인 시도 시 testAuthentication null 이어서 else 문 작동되는 듯
        Authentication testAuthentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("testAuthentication: " +testAuthentication);
        System.out.println("questionDTO: "+questionDTO);

        if (testAuthentication != null) {
            // 현재 로그인한 유저 아이디
            long currentUserId = Long.parseLong((String) testAuthentication.getPrincipal());

            // 현 로그인한 유저의 아이디로 현 로그인한 유저 엔터티 찾기  -- 질문할 유저
            UserEntity currentUser = userService.getUser(currentUserId);


            // 로그인을 한 상태라면
            if (currentUser != null) {
                try {

                    // QuestionEntity 로 변환
                    QuestionEntity questionEntity = QuestionDTO.toEntity(questionDTO);

                    // id를 null로 초기화한다. 생성 당시에는 id가 없어야 하기 때문이다.
                    questionEntity.setId(null);


                    //질문 받는 사람 아이디 설정
                    questionEntity.setReceivingUserId(spaceUser.getId());

                    // 질문 하는 사람 아이디 설정
                    questionEntity.setSendingUserId(currentUser.getId());

                    System.out.println("questionEntity: "+ questionEntity);


                    // 서비스를 이용해 질문 엔티티를 생성한다
                    List<QuestionEntity> entities = qaService.saveQuestion(questionEntity);

                    // 자바 스트림을 이요해 리턴된 엔티티 리스트를  QuestionDTO 로 변환한다.
                    List<QuestionDTO> dtos = entities.stream().map(QuestionDTO::new).collect(Collectors.toList());

                    // 변환된 QuestionDTO 리스트를 이용해 ResponseDTO 를 초기화한다.
                    ResponseDTO<QuestionDTO> responseDTO = ResponseDTO.<QuestionDTO>builder().data(dtos).build();

                    // ResponseDTO 를 리턴한다.
                    return ResponseEntity.ok().body(responseDTO);
                } catch (Exception e) {
                    // 혹시 예외가 있으면 dto 대신 error에 메시지를 넣어 리턴한다
                    String err = e.getMessage();
                    ResponseDTO<QuestionDTO> responseDTO = ResponseDTO.<QuestionDTO>builder().error(err).build();
                    return ResponseEntity.badRequest().body(responseDTO);
                }
            }
            // 로그인을 안한 상태라면
            else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();  //s
            }
        } else {
            return ResponseEntity.ok().body("질문을 하기 위해서는 로그인이 필수입니다.");
        }


    }


    // 질문 삭제 API - 남이 보낸 질문이라도 내 스페이스 내라면 삭제 가능
    @DeleteMapping("/{spaceId}/{questionId}/question/delete")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long spaceId, @PathVariable Long questionId) {
        try {
            // 이동한 스페이스 엔터티
            SpaceEntity space = spaceRepository.findById(spaceId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid space id"));

            // 현재 로그인한 유저
            org.springframework.security.core.Authentication testAuthentication = SecurityContextHolder.getContext().getAuthentication();
            // 현재 로그인한 유저아이디로 찾은 현재 유저 엔터티
            long currentUserId = Long.parseLong((String) testAuthentication.getPrincipal());
            UserEntity user = userService.getUser(currentUserId);

            // 현재 스페이스가 내 스페이스라면
            if (space.isOwnedBy(user)) {
                // 서비스를 이용해 질문 엔티티를 삭제한다
                qaService.deleteQuestionAndAnswers(questionId);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.badRequest().body("내 스페이스가 아니어서 삭제 불가능합니다.");
            }
        } catch (Exception e) {
            // 혹시 예외가 있으면 dto 대신 error 에 메시지를 넣어 리턴한다
            String err = e.getMessage();
            ResponseDTO<QuestionDTO> responseDTO = ResponseDTO.<QuestionDTO>builder().error(err).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }
}
