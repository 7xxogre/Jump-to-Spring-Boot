package com.example.main.comment;

import com.example.main.DataNotFoundException;
import com.example.main.answer.Answer;
import com.example.main.question.Question;
import com.example.main.user.SiteUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public Comment getComment(int id) {
        Optional<Comment> oc = this.commentRepository.findById(id);
        if (oc.isPresent()) {
            return oc.get();
        } else {
            throw new DataNotFoundException("comment not found");
        }
    }

    public Comment create(String content, Question question, Answer answer,
                          SiteUser siteUser) {
        Comment c = new Comment();
        c.setContent(content);
        c.setQuestion(question);
        c.setAnswer(answer);
        c.setAuthor(siteUser);
        c.setCreateDate(LocalDateTime.now());
        this.commentRepository.save(c);
        return c;
    }

    public List<Comment> getCommentList(Question question) {
        return this.commentRepository.findByQuestion(question);
    }

    public void delete(Comment comment) {
        this.commentRepository.delete(comment);
    }
}
