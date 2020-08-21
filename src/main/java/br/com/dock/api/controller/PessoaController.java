package br.com.dock.api.controller;

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

import br.com.dock.api.entity.Pessoa;
import br.com.dock.api.response.Response;
import br.com.dock.api.service.PessoaService;

@Controller
@RequestMapping(value = "/pessoas")
public class PessoaController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PessoaService pessoaService;

	@PostMapping
	public ResponseEntity<Response<Pessoa>> create(HttpServletRequest request, @RequestBody Pessoa pessoa,
			BindingResult result) {
		logger.info("[PessoaController][create][request: {}][pessoa: {}]", request.toString(), pessoa.toString());
		Response<Pessoa> response = new Response<>();
		try {
			validateCreatePartner(pessoa, result);
			if (result.hasErrors()) {
				result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
				return ResponseEntity.badRequest().body(response);
			}
			pessoa.setDataCriacao(new Date());

			Pessoa partnerPersisted = pessoaService.createOrUpdate(pessoa);
			response.setData(partnerPersisted);
			logger.info("[PessoaController][create][response: {}]", response.toString());
		} catch (Exception e) {
			response.getErrors().add(e.getMessage());
			logger.error("[PessoaController][create][response: {}]", response.toString());
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
	}

	@PutMapping
	public ResponseEntity<Response<Pessoa>> update(HttpServletRequest request, @RequestBody Pessoa pessoa,
			BindingResult result) {
		Response<Pessoa> response = new Response<>();
		logger.info("[PessoaController][update][request: {}][partner: {}]", request.toString(), pessoa.toString());
		try {
			validateUpdatePartner(pessoa, result);
			if (result.hasErrors()) {
				result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
				return ResponseEntity.badRequest().body(response);
			}

			Pessoa partnerPersisted = pessoaService.createOrUpdate(pessoa);
			response.setData(partnerPersisted);
			logger.info("[PessoaController][update][response: {}]", response.toString());
		} catch (Exception e) {
			response.getErrors().add(e.getMessage());
			logger.error("[PessoaController][update][response: {}]", response.toString());
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
	}

	@GetMapping(value = "{id}")
	public ResponseEntity<Response<Pessoa>> findById(@PathVariable Long id) {
		Response<Pessoa> response = new Response<>();
		logger.info("[PessoaController][findById][id: {}]", id);

		Optional<Pessoa> pessoa = pessoaService.findById(id);

		if (pessoa.isPresent()) {
			response.setData(pessoa.get());
			logger.info("[PessoaController][findById][response: {}]", response.toString());
		} else {
			response.getErrors().add("Register not found for id ".concat(id.toString()));
			logger.error("[PessoaController][findById][response: {}]", response.toString());
			return ResponseEntity.badRequest().body(response);
		}

		return ResponseEntity.ok(response);
	}

	@DeleteMapping(value = "{id}")
	public ResponseEntity<Response<String>> delete(@PathVariable Long id) {
		Response<String> response = new Response<>();
		logger.info("[PessoaController][delete][id: {}]", id);
		Optional<Pessoa> pessoa = pessoaService.findById(id);

		if (pessoa.isPresent()) {
			pessoaService.delete(id);
			response.setData("Pessoa ".concat(id.toString()).concat(" removed."));
			logger.info("[PessoaController][delete][response: {}]", response.toString());
		} else {
			response.getErrors().add("Register not found for id ".concat(id.toString()));
			logger.error("[PessoaController][delete][response: {}]", response.toString());
			return ResponseEntity.badRequest().body(response);
		}

		return ResponseEntity.ok(response);
	}

	@GetMapping
	public ResponseEntity<Response<List<Pessoa>>> findAll() {
		Response<List<Pessoa>> response = new Response<>();
		logger.info("[PessoaController][findAll]");

		response.setData(pessoaService.findAll());
		logger.info("[PessoaController][findAll][response: {}]", response.toString());

		return ResponseEntity.ok(response);
	}

	private void validateCreatePartner(Pessoa pessoa, BindingResult result) {
		if (pessoa.getCpf() == null) {
			result.addError(new ObjectError("Pessoa", "cpfNotInformed"));
		}
		if (pessoa.getNome() == null) {
			result.addError(new ObjectError("Pessoa", "nomeNotInformed"));
		}
	}

	private void validateUpdatePartner(Pessoa pessoa, BindingResult result) {
		if (pessoa.getCpf() == null) {
			result.addError(new ObjectError("Pessoa", "cpfNotInformed"));
		}
		if (pessoa.getNome() == null) {
			result.addError(new ObjectError("Pessoa", "nomeNotInformed"));
		}
		if (pessoa.getIdPessoa() == null) {
			result.addError(new ObjectError("Pessoa", "idPessoaNotInformed"));
		}
	}
}
