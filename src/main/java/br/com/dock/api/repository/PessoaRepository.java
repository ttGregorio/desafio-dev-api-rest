package br.com.dock.api.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import br.com.dock.api.entity.Pessoa;

public interface PessoaRepository extends CrudRepository<Pessoa, Long> {

	public List<Pessoa> findAll();
}
