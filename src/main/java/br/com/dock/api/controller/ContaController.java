package br.com.dock.api.controller;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.dock.api.dto.MovementDTO;
import br.com.dock.api.entity.Conta;
import br.com.dock.api.response.Response;
import br.com.dock.api.service.ContaService;

@Controller
@RequestMapping(value = "/contas")
public class ContaController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ContaService contaService;

	@PostMapping
	public ResponseEntity<Response<Conta>> create(HttpServletRequest request, @RequestBody Conta conta,
			BindingResult result) {
		logger.info("[ContaController][create][request: {}][conta: {}]", request.toString(), conta.toString());
		Response<Conta> response = new Response<>();
		try {
			validateCreatePartner(conta, result);
			if (result.hasErrors()) {
				result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
				return ResponseEntity.badRequest().body(response);
			}
			if (conta.getDataCriacao() == null) {
				conta.setDataCriacao(new Date());
			}
			conta.setFlagAtivo(true);

			Conta partnerPersisted = contaService.createOrUpdate(conta);
			response.setData(partnerPersisted);
			logger.info("[ContaController][create][response: {}]", response.toString());
		} catch (Exception e) {
			response.getErrors().add(e.getMessage());
			logger.error("[ContaController][create][response: {}]", response.toString());
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
	}

	@PutMapping
	public ResponseEntity<Response<Conta>> update(HttpServletRequest request, @RequestBody Conta conta,
			BindingResult result) {
		Response<Conta> response = new Response<>();
		logger.info("[ContaController][update][request: {}][partner: {}]", request.toString(), conta.toString());
		try {
			validateUpdatePartner(conta, result);
			if (result.hasErrors()) {
				result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
				return ResponseEntity.badRequest().body(response);
			}

			Conta partnerPersisted = contaService.createOrUpdate(conta);
			response.setData(partnerPersisted);
			logger.info("[ContaController][update][response: {}]", response.toString());
		} catch (Exception e) {
			response.getErrors().add(e.getMessage());
			logger.error("[ContaController][update][response: {}]", response.toString());
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
	}

	@PutMapping("/deposit")
	public ResponseEntity<Response<Conta>> deposit(HttpServletRequest request, @RequestBody MovementDTO movementDTO,
			BindingResult result) {
		Response<Conta> response = new Response<>();
		logger.info("[ContaController][deposit][request: {}][movementDTO: {}]", request.toString(),
				movementDTO.toString());
		try {
			validateMovementDTO(movementDTO, result);
			if (result.hasErrors()) {
				result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
				return ResponseEntity.badRequest().body(response);
			}

			Optional<Conta> conta = contaService.findById(movementDTO.getIdConta());

			if (conta.isPresent()) {
				response.setData(contaService.deposit(conta.get(), movementDTO));
				logger.info("[ContaController][deposit][response: {}]", response.toString());
			} else {
				response.getErrors().add("Register not found for id ".concat(movementDTO.getIdConta().toString()));
				logger.error("[ContaController][deposit][response: {}]", response.toString());
				return ResponseEntity.badRequest().body(response);
			}
		} catch (Exception e) {
			response.getErrors().add(e.getMessage());
			logger.error("[ContaController][deposit][response: {}]", response.toString());
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
	}

	@PutMapping("/saque")
	public ResponseEntity<Response<Conta>> saque(HttpServletRequest request, @RequestBody MovementDTO movementDTO,
			BindingResult result) {
		Response<Conta> response = new Response<>();
		logger.info("[ContaController][saque][request: {}][movementDTO: {}]", request.toString(),
				movementDTO.toString());
		try {
			validateMovementDTO(movementDTO, result);
			if (result.hasErrors()) {
				result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
				return ResponseEntity.badRequest().body(response);
			}

			Optional<Conta> conta = contaService.findById(movementDTO.getIdConta());

			if (conta.isPresent()) {
				if (conta.get().getSaldo().compareTo(movementDTO.getValue()) < 0) {
					response.getErrors().add("Saldo indisponível");
					logger.error("[ContaController][saque][response: {}]", response.toString());
					return ResponseEntity.badRequest().body(response);

				}
				response.setData(contaService.withdraw(conta.get(), movementDTO));
				logger.info("[ContaController][saque][response: {}]", response.toString());
			} else {
				response.getErrors().add("Register not found for id ".concat(movementDTO.getIdConta().toString()));
				logger.error("[ContaController][saque][response: {}]", response.toString());
				return ResponseEntity.badRequest().body(response);
			}
		} catch (Exception e) {
			response.getErrors().add(e.getMessage());
			logger.error("[ContaController][saque][response: {}]", response.toString());
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
	}

	@GetMapping(value = "{id}/bloqueio")
	public ResponseEntity<String> bloqueio(@PathVariable Long id) {
		logger.info("[ContaController][findById][id: {}]", id);

		try {
			Optional<Conta> conta = contaService.findById(id);

			if (conta.isPresent()) {
				conta.get().setFlagAtivo(false);
				contaService.createOrUpdate(conta.get());
				return ResponseEntity.ok("Conta Bloqueada");
			} else {
				return ResponseEntity.badRequest().body("Register not found for id ".concat(id.toString()));
			}
		} catch (Exception e) {
			logger.error("[ContaController][bloqueio][error: {}]", e.getMessage());
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@GetMapping(value = "{id}")
	public ResponseEntity<Response<Conta>> findById(@PathVariable Long id) {
		Response<Conta> response = new Response<>();
		logger.info("[ContaController][findById][id: {}]", id);

		Optional<Conta> conta = contaService.findById(id);

		if (conta.isPresent()) {
			response.setData(conta.get());
			logger.info("[ContaController][findById][response: {}]", response.toString());
		} else {
			response.getErrors().add("Register not found for id ".concat(id.toString()));
			logger.error("[ContaController][findById][response: {}]", response.toString());
			return ResponseEntity.badRequest().body(response);
		}

		return ResponseEntity.ok(response);
	}

	@GetMapping(value = "{id}/saldo")
	public ResponseEntity<String> getSaldo(@PathVariable Long id) {
		logger.info("[ContaController][getSaldo][id: {}]", id);

		Optional<Conta> conta = contaService.findById(id);

		if (conta.isPresent()) {
			logger.info("[ContaController][getSaldo][response: {}]", conta.get().getSaldo());
			return ResponseEntity.ok(conta.get().getSaldo().toString());
		} else {
			logger.error("[ContaController][getSaldo][response: {}]",
					"Register not found for id ".concat(id.toString()));
			return ResponseEntity.badRequest().body("Register not found for id ".concat(id.toString()));
		}

	}

	@DeleteMapping(value = "{id}")
	public ResponseEntity<Response<String>> delete(@PathVariable Long id) {
		Response<String> response = new Response<>();
		logger.info("[ContaController][delete][id: {}]", id);
		Optional<Conta> conta = contaService.findById(id);

		if (conta.isPresent()) {
			contaService.delete(id);
			response.setData("Conta ".concat(id.toString()).concat(" removed."));
			logger.info("[ContaController][delete][response: {}]", response.toString());
		} else {
			response.getErrors().add("Register not found for id ".concat(id.toString()));
			logger.error("[ContaController][delete][response: {}]", response.toString());
			return ResponseEntity.badRequest().body(response);
		}

		return ResponseEntity.ok(response);
	}

	@GetMapping
	public ResponseEntity<Response<List<Conta>>> findAll() {
		Response<List<Conta>> response = new Response<>();
		logger.info("[ContaController][findAll]");

		response.setData(contaService.findAll());
		logger.info("[ContaController][findAll][response: {}]", response.toString());

		return ResponseEntity.ok(response);
	}

	private void validateCreatePartner(Conta conta, BindingResult result) {
		if (conta.getTipoConta() == null) {
			result.addError(new ObjectError("Conta", "tipoContaNotInformed"));
		}
		if (conta.getPessoa() == null) {
			result.addError(new ObjectError("Conta", "pessoaNotInformed"));
		}
		if (conta.getSaldo() == null) {
			result.addError(new ObjectError("Conta", "saldoNotInformed"));
		}
		if (conta.getLimiteSaqueDiario() == null) {
			result.addError(new ObjectError("Conta", "limiteSaqueDiarioNotInformed"));
		}
	}

	private void validateUpdatePartner(Conta conta, BindingResult result) {
		if (conta.getTipoConta() == null) {
			result.addError(new ObjectError("Conta", "tipoContaNotInformed"));
		}
		if (conta.getPessoa() == null) {
			result.addError(new ObjectError("Conta", "pessoaNotInformed"));
		}
		if (conta.getSaldo() == null) {
			result.addError(new ObjectError("Conta", "saldoNotInformed"));
		}
		if (conta.getLimiteSaqueDiario() == null) {
			result.addError(new ObjectError("Conta", "limiteSaqueDiarioNotInformed"));
		}
		if (conta.getIdConta() == null) {
			result.addError(new ObjectError("Conta", "idNotInformed"));
		}
	}

	private void validateMovementDTO(MovementDTO movementDTO, BindingResult result) {
		if (movementDTO.getIdConta() == null) {
			result.addError(new ObjectError("Conta", "idContaNotInformed"));
		}
		if (movementDTO.getValue() == null) {
			result.addError(new ObjectError("Conta", "valueNotInformed"));
		}
		if (movementDTO.getValue() != null && movementDTO.getValue().compareTo(new BigDecimal(0)) <= 0) {
			result.addError(new ObjectError("Conta", "invalidValue"));
		}
	}
}
