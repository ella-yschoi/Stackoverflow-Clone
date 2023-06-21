package server.answer.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import server.answer.dto.AnswerResponseDto;
import server.answer.entity.Answer;
import server.answer.repository.AnswerRepository;
import server.exception.BusinessLogicException;
import server.exception.ExceptionCode;
import server.question.repository.QuestionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AnswerService {
    private AnswerRepository answerRepository;
    public AnswerService (AnswerRepository answerRepository){
        this.answerRepository=answerRepository;
    }
    public Answer createAnswer(Answer answer){
        return answerRepository.save(answer);
    }
    public Answer updateAnswer(Answer answer){
        Answer findAnswer = findVerifiedAnswer(answer.getAnswerId());

        Optional.ofNullable(answer.getContent())
                .ifPresent(content->findAnswer.setContent(content));
        findAnswer.setModifiedAt(LocalDateTime.now());
        return answerRepository.save(findAnswer);
    }
  /*  public Answer findAnswer(long answerId){
        return findVerifiedAnswer(answerId);
    }*/
    public List<Answer> findAnswers(){
        return answerRepository.findAll();
    }

    public void deleteAnswer(long answerId){
        answerRepository.deleteById(answerId);
    }
    private Answer findVerifiedAnswer(long answerId) {
        Optional<Answer> optionalAnswer = answerRepository.findById(answerId);
        Answer findAnswer =
                optionalAnswer.orElseThrow(() ->
                        new BusinessLogicException(ExceptionCode.ORDER_NOT_FOUND));
        return findAnswer;
    }
}
