package com.example.main.user;

import com.example.main.DataNotFoundException;
import com.example.main.answer.Answer;
import com.example.main.answer.AnswerService;
import com.example.main.question.Question;
import com.example.main.question.QuestionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final JavaMailSender mailSender;
    @Autowired
    public UserController(UserService userService, QuestionService questionService,
                          AnswerService answerService, JavaMailSender mailSender) {
        this.userService = userService;
        this.questionService = questionService;
        this.answerService = answerService;
        this.mailSender = mailSender;
    }

    @GetMapping("/signup")
    public String signup(UserCreateForm userCreateForm) {
        return "signup_form";
    }

    @PostMapping("/signup")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "signup_form";
        }

        if(!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect",
                            "2개의 패스워드가 일치하지 않습니다.");
            return "signup_form";
        }

        try {
            userService.create(userCreateForm.getUsername(),
                    userCreateForm.getEmail(), userCreateForm.getPassword1());
        } catch(DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "signup_form";
        } catch(Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "signup_form";
        }
        return "redirect:/";
    }

    @GetMapping("/login")
    public String login() {
        return "login_form";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile")
    public String profile(UserUpdateForm userUpdateForm, Model model, Principal principal,
                          @RequestParam(value="question-page", defaultValue="0") int questionPage,
                          @RequestParam(value="ans-page", defaultValue="0") int ansPage,
                          @RequestParam(value="question-vote-page", defaultValue="0") int questionVoterPage,
                          @RequestParam(value="ans-vote-page", defaultValue="0") int ansVoterPage) {
        SiteUser siteUser = this.userService.getUser(principal.getName());
        Page<Question> wroteQuestions = this.questionService.getListByAuthor(questionPage, siteUser);
        Page<Answer> wroteAnswers = this.answerService.getListByAuthor(ansPage, siteUser);
        Page<Question> votedQuestions = this.questionService.getListByVoter(questionVoterPage, siteUser);
        Page<Answer> votedAnswers = this.answerService.getListByVoter(ansVoterPage, siteUser);

        model.addAttribute("wrote_question_paging", wroteQuestions);
        model.addAttribute("wrote_answer_paging", wroteAnswers);
        model.addAttribute("voted_question_paging", votedQuestions);
        model.addAttribute("voted_answer_paging", votedAnswers);
        model.addAttribute("username", siteUser.getUsername());
        model.addAttribute("userEmail", siteUser.getEmail());
        return "profile";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/profile")
    public String update(@Valid UserUpdateForm userUpdateForm, BindingResult bindingResult,
                         Model model, Principal principal) {
        SiteUser siteUser = this.userService.getUser(principal.getName());
        Page<Question> wroteQuestions = this.questionService.getListByAuthor(0, siteUser);
        Page<Answer> wroteAnswers = this.answerService.getListByAuthor(0, siteUser);
        Page<Question> votedQuestions = this.questionService.getListByVoter(0, siteUser);
        Page<Answer> votedAnswers = this.answerService.getListByVoter(0, siteUser);

        model.addAttribute("wrote_question_paging", wroteQuestions);
        model.addAttribute("wrote_answer_paging", wroteAnswers);
        model.addAttribute("voted_question_paging", votedQuestions);
        model.addAttribute("voted_answer_paging", votedAnswers);
        model.addAttribute("username", siteUser.getUsername());
        model.addAttribute("userEmail", siteUser.getEmail());
        if (bindingResult.hasErrors()) {
            return "profile";
        }

        if(!this.userService.isMatch(userUpdateForm.getOriginPassword(), siteUser.getPassword())) {
            bindingResult.rejectValue("originPassword", "passwordInCorrect",
                    "기존 패스워드가 일치하지 않습니다.");
            return "profile";
        }
        if(!userUpdateForm.getPassword1().equals(userUpdateForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect",
                    "확인 패스워드가 일치하지 않습니다.");
            return "profile";
        }

        try {
            userService.update(siteUser, userUpdateForm.getPassword1());
        } catch(Exception e) {
            e.printStackTrace();
            bindingResult.reject("updateFailed", e.getMessage());
        }
        return "profile";
    }

    @GetMapping("/find-account")
    public String findAccount(Model model) {
        model.addAttribute("sendConfirm", false);
        model.addAttribute("error", false);
        return "find_account";
    }

    @PostMapping("/find-account")
    public String findAccount(Model model, @RequestParam(value="email") String email) {
        try {
            SiteUser siteUser = this.userService.getUserByEmail(email);
            model.addAttribute("sendConfirm", true);
            model.addAttribute("userEmail", email);
            model.addAttribute("error", false);
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setTo(email);
            simpleMailMessage.setSubject("계정 정보입니다.");
            StringBuffer sb = new StringBuffer();

            String newPassword = PasswordGenerator.generateRandomPassword();
            sb.append(siteUser.getUsername()).append("계정의 비밀번호를 새롭게 초기화 했습니다..\n").append("새 비밀번호는 ")
                    .append(newPassword).append("입니다.\n")
                    .append("로그인 후 내 정보에서 새로 비밀번호를 지정해주세요.");
            simpleMailMessage.setText(sb.toString());
            this.userService.update(siteUser, newPassword);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mailSender.send(simpleMailMessage);
                }
            }).start();
        } catch(DataNotFoundException e) {
            model.addAttribute("sendConfirm", false);
            model.addAttribute("error", true);
        }
        return "find_account";
    }


    public static class PasswordGenerator {
        private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
        private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
        private static final String NUMBER = "0123456789";
        private static final String OTHER_CHAR = "!@#$%&*()_+-=[]?";

        private static final String PASSWORD_ALLOW_BASE = CHAR_LOWER + CHAR_UPPER + NUMBER + OTHER_CHAR;
        private static final int PASSWORD_LENGTH = 12;

        public static String generateRandomPassword() {
            if (PASSWORD_LENGTH < 1) throw new IllegalArgumentException("Password length must be at least 1");

            StringBuilder sb = new StringBuilder(PASSWORD_LENGTH);
            Random random = new SecureRandom();
            for (int i = 0; i < PASSWORD_LENGTH; i++) {
                int rndCharAt = random.nextInt(PASSWORD_ALLOW_BASE.length());
                char rndChar = PASSWORD_ALLOW_BASE.charAt(rndCharAt);
                sb.append(rndChar);
            }

            return sb.toString();
        }
    }
}
