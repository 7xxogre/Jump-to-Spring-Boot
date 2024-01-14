package com.example.main.answer;

import com.example.main.answer.Answer;
import com.example.main.question.Question;
import com.example.main.user.SiteUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Integer> {
    Page<Answer> findByQuestion(Question question, Pageable pageable);

    Page<Answer> findByAuthor(SiteUser siteUser, Pageable pageable);

    Page<Answer> findAll(Specification<Answer> spec, Pageable pageable);
}
