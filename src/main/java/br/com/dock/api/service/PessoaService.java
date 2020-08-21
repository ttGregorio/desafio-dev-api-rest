package br.com.dock.api.service;

import java.util.List;
import java.util.Optional;

import br.com.dock.api.entity.Pessoa;

public interface PessoaService {

	Pessoa createOrUpdate(Pessoa pessoa);

	Optional<Pessoa> findById(Long id);

	void delete(Long id);

	List<Pessoa> findAll();

}