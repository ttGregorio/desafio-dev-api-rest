package br.com.dock.api.controller;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import br.com.dock.api.entity.Conta;
import br.com.dock.api.entity.Pessoa;
import br.com.dock.api.response.Response;
import br.com.dock.api.service.ContaService;

@RunWith(MockitoJUnitRunner.Silent.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ContaControllerTest {

	@Autowired
	protected MockMvc mockMvc;

	@InjectMocks
	private ContaController contaController;

	@Mock
	private ContaService contaService;

	private Date testDate;

	@Before
	public void contextLoads() throws JsonParseException, JsonMappingException, IOException {
		this.mockMvc = MockMvcBuilders.standaloneSetup(contaController).build();

		testDate = new Date();

		when(contaService.createOrUpdate(getContaToCreate())).thenReturn(getContaCreated());
		when(contaService.findById(Long.getLong("1"))).thenReturn(Optional.of(getContaCreated()));
		when(contaService.findAll()).thenReturn(getContaList());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testCreateSuccessfull() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "123123123");
		try {
			MvcResult result = this.mockMvc
					.perform(post("/").headers(headers).content(new Gson().toJson(getContaToCreate()))
							.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk()).andReturn();
			assertNotNull(result.getResponse());

			ObjectMapper mapper = new ObjectMapper();

			Response<LinkedHashMap> resultConta = mapper.readValue(result.getResponse().getContentAsString(),
					Response.class);
			assertTrue(resultConta.getData().get("id").equals(getContaCreated().getIdConta()));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testCreateWithoutUsername() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "123123123");
		try {
			this.mockMvc
					.perform(post("/").headers(headers).content(new Gson().toJson(getContaWithoutName()))
							.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isBadRequest()).andReturn();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testUpdateSuccessfull() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "123123123");
		try {
			MvcResult result = this.mockMvc
					.perform(put("/").headers(headers).content(new Gson().toJson(getContaToCreate()))
							.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk()).andReturn();
			assertNotNull(result.getResponse());

			ObjectMapper mapper = new ObjectMapper();

			Response<LinkedHashMap> resultConta = mapper.readValue(result.getResponse().getContentAsString(),
					Response.class);
			assertTrue(resultConta.getData().get("id").equals(getContaCreated().getIdConta()));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testUpdateWithoutUserName() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "123123123");
		try {
			this.mockMvc
					.perform(post("/").headers(headers).content(new Gson().toJson(getContaWithoutName()))
							.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isBadRequest()).andReturn();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testFindByIdExists() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "123123123");
		try {
			MvcResult result = this.mockMvc.perform(get("/1").headers(headers).accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk()).andReturn();

			ObjectMapper mapper = new ObjectMapper();

			@SuppressWarnings({ "unchecked", "rawtypes" })
			Response<LinkedHashMap> resultConta = mapper.readValue(result.getResponse().getContentAsString(),
					Response.class);
			assertTrue(resultConta.getData().get("id").equals(getContaCreated().getIdConta()));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testFindByIdDoesntExists() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "123123123");
		try {
			this.mockMvc.perform(get("/2").headers(headers).accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isBadRequest()).andReturn();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testDeleteIdExists() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "123123123");
		try {
			this.mockMvc.perform(delete("/1").headers(headers).accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk()).andReturn();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testDeleteIdDoesntExists() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "123123123");
		try {
			this.mockMvc.perform(delete("/2").headers(headers).accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isBadRequest()).andReturn();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testFindAll() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "123123123");
		try {
			MvcResult result = this.mockMvc.perform(get("/0/5").headers(headers).accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk()).andReturn();

			assertNotNull(result.getResponse());

			ObjectMapper mapper = new ObjectMapper();

			Response<LinkedHashMap> resultConta = mapper.readValue(result.getResponse().getContentAsString(),
					Response.class);

			assertTrue(resultConta.getData().size() == 11);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/* Mock de Dados */
	private Conta getContaToCreate() {
		return new Conta(null, new Pessoa(Long.getLong("1"), "Nome", "cpf", testDate), new BigDecimal(0),
				new BigDecimal(0), true, Long.getLong("1"), testDate);
	}

	private Conta getContaWithoutName() {
		return new Conta(null, new Pessoa(Long.getLong("1"), null, "cpf", testDate), new BigDecimal(0),
				new BigDecimal(0), true, Long.getLong("1"), testDate);
	}

	private Conta getContaCreated() {
		return new Conta(Long.getLong("1"), new Pessoa(Long.getLong("1"), null, "cpf", testDate), new BigDecimal(0),
				new BigDecimal(0), true, Long.getLong("1"), testDate);
	}

	private List<Conta> getContaList() {
		List<Conta> list = new ArrayList<Conta>();

		for (int i = 0; i < 10; i++) {
			list.add(new Conta(Long.getLong("1"), new Pessoa(Long.getLong("1"), null, "cpf", testDate),
					new BigDecimal(0), new BigDecimal(0), true, Long.getLong("1"), testDate));
		}
		return list;
	}

}
