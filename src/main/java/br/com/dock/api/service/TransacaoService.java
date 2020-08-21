package br.com.dock.api.service;

import java.util.List;
import java.util.Optional;

import br.com.dock.api.dto.TransacaoSearchDTO;
import br.com.dock.api.entity.Transacao;

public interface TransacaoService {

	Transacao createOrUpdate(Transacao transacao);

	Optional<Transacao> findById(Long id);

	void delete(Long id);

	List<Transacao> findAll(Long idConta);

	List<Transacao> search(TransacaoSearchDTO transacao);

}