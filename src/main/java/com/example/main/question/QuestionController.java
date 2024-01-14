package com.example.main.question;

import com.example.main.answer.Answer;
import com.example.main.answer.AnswerForm;
import com.example.main.answer.AnswerService;
import com.example.main.user.SiteUser;
import com.example.main.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RequestMapping("/question")
@Controller
public class QuestionController {
    private final QuestionService questionService;
    private final UserService userService;
    private final AnswerService answerService;

    @Autowired
    public QuestionController(QuestionService questionService, UserService userService,
                              AnswerService answerService) {
        this.questionService = questionService;
        this.userService = userService;
        this.answerService = answerService;
    }

//    @GetMapping("/list")
//    public String list(Model model) {
//        List<Question> questions = this.questionService.getList();
//        model.addAttribute("questionList", questions);
//        return "question_list";
//    }

    @GetMapping("/list")
    public String list(Model model, @RequestParam(value="page", defaultValue="0") int page,
                       @RequestParam(value = "kw", defaultValue = "") String kw) {
        Page<Question> paging = this.questionService.getList(page, kw);
        model.addAttribute("paging", paging);
        model.addAttribute("kw", kw);
        return "question_list";
    }


    @GetMapping(value = "/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id, AnswerForm answerForm,
                         @RequestParam(value="ans-page", defaultValue="0") int answerPage,
                         @RequestParam(value="ans-ordering", defaultValue="time") String answerOrderMethod) {
        this.questionService.viewUp(id);
        Question question = this.questionService.getQuestion(id);
        Page<Answer> answerPaging = this.answerService.getAnswerList(question,
                                                            answerPage, answerOrderMethod);
        model.addAttribute("question", question);
        model.addAttribute("ans_paging", answerPaging);
        return "question_detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/create")
    public String questionCreate(QuestionForm questionForm) {
        return "question_form";
    }

//    @PostMapping(value = "/create")
//    public String questionCreate(@RequestParam(value="subject") String subject, @RequestParam(value="content") String content) {
//        this.questionService.create(subject, content);
//        return "redirect:/question/list";
//    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value="create")
    public String questionCreate(@Valid QuestionForm questionForm, BindingResult bindingResult,
                                 Principal principal) {
        if (bindingResult.hasErrors()) {
            return "question_form";
        }
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.questionService.create(questionForm.getSubject(), questionForm.getContent(), siteUser);
        return "redirect:/question/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String questionModify(QuestionForm questionForm, @PathVariable("id") Integer id,
                                 Principal principal) {
        Question question = this.questionService.getQuestion(id);
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
        }

        questionForm.setSubject(question.getSubject());
        questionForm.setContent(question.getContent());
        return "question_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String questionModify(@Valid QuestionForm questionForm, BindingResult bindingResult,
                                 Principal principal, @PathVariable("id") Integer id) {
        if (bindingResult.hasErrors()) {
            return "question_form";
        }
        Question question = this.questionService.getQuestion(id);
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
        }
        this.questionService.modify(question, questionForm.getSubject(), questionForm.getContent());
        return String.format("redirect:/question/detail/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String questionDelete(Principal principal, @PathVariable("id") Integer id) {
        Question question = this.questionService.getQuestion(id);
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
        }
        this.questionService.delete(question);
        return "redirect:/";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String questionVote(Principal principal, @PathVariable("id") Integer id) {
        Question question = this.questionService.getQuestion(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.questionService.vote(question, siteUser);
        return String.format("redirect:/question/detail/%s", id);
    }
}