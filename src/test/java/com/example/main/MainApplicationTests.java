package com.example.main;

import com.example.main.answer.Answer;
import com.example.main.answer.AnswerRepository;
import com.example.main.answer.AnswerService;
import com.example.main.category.CategoryService;
import com.example.main.comment.CommentService;
import com.example.main.question.Question;
import com.example.main.question.QuestionRepository;
import com.example.main.question.QuestionService;
import com.example.main.user.SiteUser;
import com.example.main.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class MainApplicationTests {

	@Autowired
	private QuestionService questionService;
	@Autowired
	private AnswerService answerService;
	@Autowired
	private UserService userService;
	@Autowired
	private CommentService commentService;
	@Autowired
	private CategoryService categoryService;

//	@Transactional
	@Test
	void testJpa() {
	}
	@Transactional
	@Test
	void testCategory() {
		this.questionService.create("게시판 테스트", "테스트테스트", this.categoryService.getCategoryByName("게시판1"), userService.getUser("temp"));
	}
}
