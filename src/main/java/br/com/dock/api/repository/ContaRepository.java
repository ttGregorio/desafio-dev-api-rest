package br.com.dock.api.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import br.com.dock.api.entity.Conta;

public interface ContaRepository extends CrudRepository<Conta, Long> {

	public List<Conta> findAll();

}
