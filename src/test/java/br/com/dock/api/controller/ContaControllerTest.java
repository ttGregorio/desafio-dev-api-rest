package br.com.dock.api.controller;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import br.com.dock.api.dto.MovementDTO;
import br.com.dock.api.entity.Conta;
import br.com.dock.api.entity.Pessoa;
import br.com.dock.api.response.Response;
import br.com.dock.api.service.ContaService;

@RunWith(MockitoJUnitRunner.Silent.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ContaControllerTest {

	@Autowired
	protected MockMvc mockMvc;

	@InjectMocks
	private ContaController contaController;

	@Mock
	private ContaService contaService;

	@BeforeEach
	public void contextLoads() throws Exception {
		this.mockMvc = MockMvcBuilders.standaloneSetup(contaController).build();

		when(contaService.createOrUpdate(getContaToCreate())).thenReturn(getContaCreated());
		when(contaService.findById(Long.valueOf(1))).thenReturn(Optional.of(getContaCreated()));
		when(contaService.findAll()).thenReturn(getContaList());
		when(contaService.deposit(getContaCreated(), getMovementDTO())).thenReturn(getContaDeposit());
		when(contaService.withdraw(getContaCreated(), getMovementDTO())).thenReturn(getContaDeposit());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	void testCreate() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "123123123");
		try {
			MvcResult result = this.mockMvc
					.perform(post("/contas").headers(headers).content(new Gson().toJson(getContaToCreate()))
							.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk()).andReturn();
			assertNotNull(result.getResponse());

			ObjectMapper mapper = new ObjectMapper();

			Response<LinkedHashMap> resultProductType = mapper.readValue(result.getResponse().getContentAsString(),
					Response.class);

			assertTrue(resultProductType.getData().get("idConta").toString()
					.equals(getContaCreated().getIdConta().toString()));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testCreateWithoutPessoa() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "123123123");
		try {
			this.mockMvc
					.perform(post("/contas").headers(headers).content(new Gson().toJson(getContaWithoutPessoa()))
							.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isBadRequest()).andReturn();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testCreateWithoutTipoConta() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "123123123");
		try {
			this.mockMvc
					.perform(post("/contas").headers(headers).content(new Gson().toJson(getContaWithoutTipoConta()))
							.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isBadRequest()).andReturn();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testCreateWithoutSaldo() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "123123123");
		try {
			this.mockMvc
					.perform(post("/contas").headers(headers).content(new Gson().toJson(getContaWithoutSaldo()))
							.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isBadRequest()).andReturn();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testCreateWithoutLimiteSaque() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "123123123");
		try {
			this.mockMvc
					.perform(post("/contas").headers(headers).content(new Gson().toJson(getContaWithoutLimiteSaque()))
							.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isBadRequest()).andReturn();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	void testDeposit() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "123123123");
		try {
			MvcResult result = this.mockMvc
					.perform(put("/contas/deposit").headers(headers).content(new Gson().toJson(getMovementDTO()))
							.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk()).andReturn();
			assertNotNull(result.getResponse());

			ObjectMapper mapper = new ObjectMapper();

			Response<LinkedHashMap> resultProductType = mapper.readValue(result.getResponse().getContentAsString(),
					Response.class);

			assertTrue(resultProductType.getData().get("saldo").toString().equals("10"));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void testDepositWithoutConta() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "123123123");
		try {
			this.mockMvc
					.perform(put("/contas/deposit").headers(headers)
							.content(new Gson().toJson(getMovementDTOWithoutConta()))
							.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isBadRequest()).andReturn();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void testDepositWithoutValue() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "123123123");
		try {
			this.mockMvc
					.perform(put("/contas/deposit").headers(headers)
							.content(new Gson().toJson(getMovementDTOWithoutValue()))
							.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isBadRequest()).andReturn();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	void testSaque() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "123123123");
		try {
			MvcResult result = this.mockMvc
					.perform(put("/contas/saque").headers(headers).content(new Gson().toJson(getMovementDTO()))
							.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk()).andReturn();
			assertNotNull(result.getResponse());

			ObjectMapper mapper = new ObjectMapper();

			Response<LinkedHashMap> resultProductType = mapper.readValue(result.getResponse().getContentAsString(),
					Response.class);

			assertTrue(resultProductType.getData().get("saldo").toString().equals("10"));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void testSaqueWithoutConta() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "123123123");
		try {
			this.mockMvc
					.perform(put("/contas/saque").headers(headers)
							.content(new Gson().toJson(getMovementDTOWithoutConta()))
							.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isBadRequest()).andReturn();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void testSaqueWithoutValue() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "123123123");
		try {
			this.mockMvc
					.perform(put("/contas/saque").headers(headers)
							.content(new Gson().toJson(getMovementDTOWithoutValue()))
							.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isBadRequest()).andReturn();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	void testUpdate() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "123123123");
		try {
			MvcResult result = this.mockMvc
					.perform(post("/contas").headers(headers).content(new Gson().toJson(getContaToCreate()))
							.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk()).andReturn();
			assertNotNull(result.getResponse());

			ObjectMapper mapper = new ObjectMapper();

			Response<LinkedHashMap> resultProductType = mapper.readValue(result.getResponse().getContentAsString(),
					Response.class);

			assertTrue(resultProductType.getData().get("idConta").toString()
					.equals(getContaCreated().getIdConta().toString()));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testUpdateWithoutPessoa() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "123123123");
		try {
			this.mockMvc
					.perform(put("/contas").headers(headers).content(new Gson().toJson(getContaWithoutPessoa()))
							.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isBadRequest()).andReturn();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testUpdateWithoutTipoConta() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "123123123");
		try {
			this.mockMvc
					.perform(put("/contas").headers(headers).content(new Gson().toJson(getContaWithoutTipoConta()))
							.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isBadRequest()).andReturn();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testUpdateWithoutSaldo() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "123123123");
		try {
			this.mockMvc
					.perform(put("/contas").headers(headers).content(new Gson().toJson(getContaWithoutSaldo()))
							.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isBadRequest()).andReturn();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testUpdateWithoutLimiteSaque() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "123123123");
		try {
			this.mockMvc
					.perform(put("/contas").headers(headers).content(new Gson().toJson(getContaWithoutLimiteSaque()))
							.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isBadRequest()).andReturn();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/* Mock de Dados */
	private Conta getContaToCreate() {
		return new Conta(null, new Pessoa(Long.valueOf(1), "Nome Pessoa", "Cpf Pessoa", null), new BigDecimal(0),
				new BigDecimal(0), true, Long.valueOf(1), null);
	}

	private Conta getContaWithoutPessoa() {
		return new Conta(null, null, new BigDecimal(0), new BigDecimal(0), true, Long.valueOf(1), null);
	}

	private Conta getContaWithoutTipoConta() {
		return new Conta(null, new Pessoa(Long.valueOf(1), "Nome Pessoa", "Cpf Pessoa", null), new BigDecimal(0),
				new BigDecimal(0), true, null, null);
	}

	private Conta getContaWithoutSaldo() {
		return new Conta(null, new Pessoa(Long.valueOf(1), "Nome Pessoa", "Cpf Pessoa", null), null, new BigDecimal(0),
				true, Long.valueOf(1), null);
	}

	private Conta getContaWithoutLimiteSaque() {
		return new Conta(null, new Pessoa(Long.valueOf(1), "Nome Pessoa", "Cpf Pessoa", null), new BigDecimal(0), null,
				true, Long.valueOf(1), null);
	}

	private Conta getContaCreated() {
		return new Conta(Long.valueOf(1), new Pessoa(Long.valueOf(1), "Nome Pessoa", "Cpf Pessoa", null),
				new BigDecimal(10), new BigDecimal(0), true, Long.valueOf(1), null);
	}

	private Conta getContaDeposit() {
		return new Conta(Long.valueOf(1), new Pessoa(Long.valueOf(1), "Nome Pessoa", "Cpf Pessoa", null),
				new BigDecimal(10), new BigDecimal(0), true, Long.valueOf(1), null);
	}

	private MovementDTO getMovementDTO() {
		return new MovementDTO(Long.valueOf(1), new BigDecimal(10));
	}

	private MovementDTO getMovementDTOWithoutConta() {
		return new MovementDTO(null, new BigDecimal(10));
	}

	private MovementDTO getMovementDTOWithoutValue() {
		return new MovementDTO(Long.valueOf(1), null);
	}

	private List<Conta> getContaList() {
		List<Conta> list = new ArrayList<>();

		for (int i = 0; i < 10; i++) {
			list.add(new Conta(Long.valueOf(i), new Pessoa(Long.getLong("1"), "Nome Pessoa", "Cpf Pessoa", null),
					new BigDecimal(0), new BigDecimal(0), true, Long.valueOf(1), null));
		}
		return list;
	}
}
