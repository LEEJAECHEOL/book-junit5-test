package com.cos.book.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import com.cos.book.domain.Book;
import com.cos.book.domain.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
public class BookControllerIntegreTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private EntityManager entityManager;

	@BeforeEach
	public void init() {
		entityManager.createNativeQuery("ALTER TABLE book AUTO_INCREMENT = 1").executeUpdate();
	}
	
	@Test
	public void save_테스트() throws Exception {
		Book book = new Book(null, "시험 1", 4.0, 4000.0);
		String content = new ObjectMapper().writeValueAsString(book);
		
		ResultActions resultActions = mockMvc.perform(post("/book")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(content)
				.accept(MediaType.APPLICATION_JSON_UTF8));
		
		resultActions.andExpect(status().isCreated())
				.andExpect(jsonPath("$.title").value("시험 1"))
				.andDo(MockMvcResultHandlers.print());
		
	}
	@Test
	public void findAll_테스트() throws Exception {
		// given
		List<Book> books = new ArrayList<>();
		books.add(new Book(null, "시험 1", 4.0, 4000.0));
		books.add(new Book(null, "시험 2", 4.0, 4000.0));
		books.add(new Book(null, "시험 3", 4.0, 4000.0));
		bookRepository.saveAll(books);

		ResultActions resultAction = mockMvc.perform(get("/book").accept(MediaType.APPLICATION_JSON_UTF8));

		resultAction.andExpect(status().isOk()).andExpect(jsonPath("$.[0].id").value(1L))
				.andExpect(jsonPath("$", Matchers.hasSize(3))).andExpect(jsonPath("$.[0].title").value("시험 1"))
				.andDo(MockMvcResultHandlers.print());
	}
	
	@Test
	public void findById_테스트() throws Exception {
		// given
		Integer id = 1;

		List<Book> books = new ArrayList<>();
		books.add(new Book(null, "시험 1", 4.0, 4000.0));
		bookRepository.saveAll(books);

		// when
		ResultActions resultAction = mockMvc.perform(get("/book/{id}", id).accept(MediaType.APPLICATION_JSON_UTF8));

		// then
		resultAction.andExpect(status().isOk()).andExpect(jsonPath("$.title").value("시험 1"))
				.andDo(MockMvcResultHandlers.print());
	}
	@Test
	public void update_테스트() throws Exception {
		// given
		Integer id = 1;
		List<Book> books = new ArrayList<>();
		books.add(new Book(null, "시험 1", 4.0, 4000.0));
		books.add(new Book(null, "시험 2", 4.0, 4000.0));
		books.add(new Book(null, "시험 3", 4.0, 4000.0));
		bookRepository.saveAll(books);

		Book book = new Book(null, "시험입니다.", 4.0, 5000);
		String content = new ObjectMapper().writeValueAsString(book);

		// when
		ResultActions resultAction = mockMvc.perform(put("/book/{id}", id).contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(content).accept(MediaType.APPLICATION_JSON_UTF8));

		// then
		resultAction.andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.title").value("시험입니다.")).andDo(MockMvcResultHandlers.print());
	}
	@Test
	public void delete_테스트() throws Exception {
		// given
		Integer id = 1;
		List<Book> books = new ArrayList<>();
		books.add(new Book(null, "시험 1", 4.0, 4000.0));
		books.add(new Book(null, "시험 2", 4.0, 4000.0));
		books.add(new Book(null, "시험 3", 4.0, 4000.0));
		bookRepository.saveAll(books);
		
		ResultActions resultAction = mockMvc.perform(delete("/book/{id}",id)
				.accept(MediaType.APPLICATION_JSON_UTF8));

		resultAction
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data").value("ok"))
			.andDo(MockMvcResultHandlers.print());
		
	}
	
}
