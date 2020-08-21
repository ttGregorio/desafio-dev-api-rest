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

import br.com.dock.api.dto.TransacaoSearchDTO;
import br.com.dock.api.entity.Transacao;
import br.com.dock.api.response.Response;
import br.com.dock.api.service.TransacaoService;

@Controller
@RequestMapping(value = "/transacoes")
public class TransacaoController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private TransacaoService transacaoService;

	@PostMapping
	public ResponseEntity<Response<Transacao>> create(HttpServletRequest request, @RequestBody Transacao transacao,
			BindingResult result) {
		logger.info("[TransacaoController][create][request: {}][transacao: {}]", request.toString(),
				transacao.toString());
		Response<Transacao> response = new Response<>();
		try {
			validateCreateTransacao(transacao, result);
			if (result.hasErrors()) {
				result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
				return ResponseEntity.badRequest().body(response);
			}
			transacao.setDataTransacao(new Date());

			Transacao partnerPersisted = transacaoService.createOrUpdate(transacao);
			response.setData(partnerPersisted);
			logger.info("[TransacaoController][create][response: {}]", response.toString());
		} catch (Exception e) {
			response.getErrors().add(e.getMessage());
			logger.error("[TransacaoController][create][response: {}]", response.toString());
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
	}

	@PutMapping
	public ResponseEntity<Response<Transacao>> update(HttpServletRequest request, @RequestBody Transacao transacao,
			BindingResult result) {
		Response<Transacao> response = new Response<>();
		logger.info("[TransacaoController][update][request: {}][partner: {}]", request.toString(),
				transacao.toString());
		try {
			validateUpdateTransacao(transacao, result);
			if (result.hasErrors()) {
				result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
				return ResponseEntity.badRequest().body(response);
			}

			Transacao partnerPersisted = transacaoService.createOrUpdate(transacao);
			response.setData(partnerPersisted);
			logger.info("[TransacaoController][update][response: {}]", response.toString());
		} catch (Exception e) {
			response.getErrors().add(e.getMessage());
			logger.error("[TransacaoController][update][response: {}]", response.toString());
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
	}

	@GetMapping(value = "{id}/id")
	public ResponseEntity<Response<Transacao>> findById(@PathVariable Long id) {
		Response<Transacao> response = new Response<>();
		logger.info("[TransacaoController][findById][id: {}]", id);

		Optional<Transacao> transacao = transacaoService.findById(id);

		if (transacao.isPresent()) {
			response.setData(transacao.get());
			logger.info("[TransacaoController][findById][response: {}]", response.toString());
		} else {
			response.getErrors().add("Register not found for id ".concat(id.toString()));
			logger.error("[TransacaoController][findById][response: {}]", response.toString());
			return ResponseEntity.badRequest().body(response);
		}

		return ResponseEntity.ok(response);
	}

	@DeleteMapping(value = "{id}")
	public ResponseEntity<Response<String>> delete(@PathVariable Long id) {
		Response<String> response = new Response<>();
		logger.info("[TransacaoController][delete][id: {}]", id);
		Optional<Transacao> transacao = transacaoService.findById(id);

		if (transacao.isPresent()) {
			transacaoService.delete(id);
			response.setData("Transacao ".concat(id.toString()).concat(" removed."));
			logger.info("[TransacaoController][delete][response: {}]", response.toString());
		} else {
			response.getErrors().add("Register not found for id ".concat(id.toString()));
			logger.error("[TransacaoController][delete][response: {}]", response.toString());
			return ResponseEntity.badRequest().body(response);
		}

		return ResponseEntity.ok(response);
	}

	@GetMapping(value = "{idConta}")
	public ResponseEntity<Response<List<Transacao>>> findAll(@PathVariable Long idConta) {
		Response<List<Transacao>> response = new Response<>();
		logger.info("[TransacaoController][findAll][idConta: {}]", idConta);

		response.setData(transacaoService.findAll(idConta));
		logger.info("[TransacaoController][findAll][response: {}]", response.toString());

		return ResponseEntity.ok(response);
	}

	@PostMapping(value = "search")
	public ResponseEntity<Response<List<Transacao>>> find(HttpServletRequest request,
			@RequestBody TransacaoSearchDTO transacao, BindingResult result) {
		logger.info("[TransacaoController][find][request: {}][transacao: {}]", request.toString(),
				transacao.toString());
		Response<List<Transacao>> response = new Response<>();
		try {
			validateTransacaoDTO(transacao, result);
			if (result.hasErrors()) {
				result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
				return ResponseEntity.badRequest().body(response);
			}

			response.setData(transacaoService.search(transacao));
			logger.info("[TransacaoController][create][response: {}]", response.toString());
		} catch (Exception e) {
			response.getErrors().add(e.getMessage());
			logger.error("[TransacaoController][create][response: {}]", response.toString());
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
	}

	private void validateCreateTransacao(Transacao transacao, BindingResult result) {
		if (transacao.getConta() == null) {
			result.addError(new ObjectError("Transacao", "contaNotInformed"));
		}
		if (transacao.getValor() == null) {
			result.addError(new ObjectError("Transacao", "valorNotInformed"));
		}
	}

	private void validateUpdateTransacao(Transacao transacao, BindingResult result) {
		if (transacao.getDataTransacao() == null) {
			result.addError(new ObjectError("Transacao", "dataTransacaoNotInformed"));
		}
		if (transacao.getConta() == null) {
			result.addError(new ObjectError("Transacao", "contaNotInformed"));
		}
		if (transacao.getValor() == null) {
			result.addError(new ObjectError("Transacao", "valorNotInformed"));
		}
		if (transacao.getIdTransacao() == null) {
			result.addError(new ObjectError("Transacao", "idTransacaoNotInformed"));
		}
	}

	private void validateTransacaoDTO(TransacaoSearchDTO transacao, BindingResult result) {
		if (transacao.getIdConta() == null) {
			result.addError(new ObjectError("TransacaoSearchDTO", "idContaNotInformed"));
		}
	}
}
