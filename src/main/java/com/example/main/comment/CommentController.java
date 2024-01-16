package com.example.main.comment;


import com.example.main.answer.Answer;
import com.example.main.answer.AnswerService;
import com.example.main.question.Question;
import com.example.main.question.QuestionService;
import com.example.main.user.SiteUser;
import com.example.main.user.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@RequestMapping("/comment")
@Controller
public class CommentController {

    private final CommentService commentService;
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final UserService userService;

    @Autowired
    public CommentController(CommentService commentService, QuestionService questionService,
                             AnswerService answerService, UserService userService) {
        this.commentService = commentService;
        this.questionService = questionService;
        this.answerService = answerService;
        this.userService = userService;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value="/create/question/{id}")
    public String questionCommentCreate(Model model, @PathVariable("id") Integer id,
                                @Valid CommentForm commentForm, BindingResult bindingResult,
                                Principal principal) {
        Question question = this.questionService.getQuestion(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        if (bindingResult.hasErrors()) {
            model.addAttribute("question", question);
            return "question_detail";
        }
        Comment comment = this.commentService.create(commentForm.getContent(), question, null, siteUser);
        return String.format("redirect:/question/detail/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value="/create/answer/{id}")
    public String answerCommentCreate(Model model, @PathVariable("id") Integer id,
                                        @Valid CommentForm commentForm, BindingResult bindingResult,
                                        Principal principal) {
        Answer answer = this.answerService.getAnswer(id);
        Question question = answer.getQuestion();
        SiteUser siteUser = this.userService.getUser(principal.getName());
        if (bindingResult.hasErrors()) {
            model.addAttribute("question", question);
            return "question_detail";
        }
        Comment comment = this.commentService.create(commentForm.getContent(), question, answer, siteUser);
        return String.format("redirect:/question/detail/%s#answer_%s", question.getId(), id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value="/delete/{id}")
    public String commentDelete(Model model, @PathVariable("id") Integer id,
                                Principal principal) {
        Comment comment = this.commentService.getComment(id);
        if (!comment.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
        }
        this.commentService.delete(comment);
        return String.format("redirect:/question/detail/%s", comment.getQuestion().getId());
    }
}