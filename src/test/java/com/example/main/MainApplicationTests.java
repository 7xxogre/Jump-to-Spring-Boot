package com.example.main;

import com.example.main.answer.Answer;
import com.example.main.answer.AnswerRepository;
import com.example.main.answer.AnswerService;
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

//	@Transactional
	@Test
	void testJpa() {
		List<Question> questionLst = this.questionService.getList();
		Question question = questionLst.get(questionLst.size() - 1);
		SiteUser user = userService.create("temp", "temp@temp.com", "1234");
		for (int i = 0; i < 300; ++i) {
			this.answerService.create(question, String.format("테스트 댓글 %s!!", i), user);
		}
	}
}
