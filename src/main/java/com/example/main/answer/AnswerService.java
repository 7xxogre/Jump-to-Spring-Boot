package com.example.main.answer;


import com.example.main.DataNotFoundException;
import com.example.main.question.Question;
import com.example.main.user.SiteUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AnswerService {

    private final AnswerRepository answerRepository;

    @Autowired
    public AnswerService(AnswerRepository answerRepository) {
        this.answerRepository = answerRepository;
    }

    public Answer create(Question question, String content, SiteUser siteUser) {
        Answer answer = new Answer();
        answer.setContent(content);
        answer.setQuestion(question);
        answer.setAuthor(siteUser);
        answer.setCreateDate(LocalDateTime.now());
        this.answerRepository.save(answer);
        return answer;
    }

    public Answer getAnswer(Integer id) {
        Optional<Answer> answer = this.answerRepository.findById(id);
        if (answer.isPresent()) {
            return answer.get();
        } else {
            throw new DataNotFoundException("answer not found");
        }
    }
    public void modify(Answer answer, String content) {
        answer.setContent(content);
        answer.setModifyDate(LocalDateTime.now());
        this.answerRepository.save(answer);
    }

    public void delete(Answer answer) {
        this.answerRepository.delete(answer);
    }

    public void vote(Answer answer, SiteUser siteUser) {
        answer.getVoter().add(siteUser);
        this.answerRepository.save(answer);
    }

    public Page<Answer> getAnswerList(Question question, int page, String answerOrderMethod) {
        List<Sort.Order> sorts = new ArrayList<>();
        if (answerOrderMethod.startsWith("recommend")) {
            sorts.add(Sort.Order.desc("voter"));
        }
        else {
            sorts.add(Sort.Order.desc("createDate"));
        }
        Pageable pageable = PageRequest.of(page, 5, Sort.by(sorts));
        return this.answerRepository.findByQuestion(question, pageable);
    }
}
