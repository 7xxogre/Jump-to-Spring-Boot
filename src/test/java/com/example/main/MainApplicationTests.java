package com.example.main;

import com.example.main.answer.Answer;
import com.example.main.answer.AnswerRepository;
import com.example.main.answer.AnswerService;
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

	@Transactional
	@Test
	void testJpa() {
		List<Question> questionLst = this.questionService.getList();
		Question question = questionLst.get(questionLst.size() - 1);
//		SiteUser user = this.userService.create("temp", "temp@temp.com", "1234");
		SiteUser user = this.userService.getUser("temp");

		this.commentService.create("테스트 질문 댓글", question, null, user);
		Answer answer = this.answerService.create(question, "테스트 답변", user);
		this.commentService.create("테스트 답변 댓글", question, answer, user);
	}
}
