package br.com.dock.api.service;

import java.util.List;
import java.util.Optional;

import br.com.dock.api.dto.MovementDTO;
import br.com.dock.api.entity.Conta;

public interface ContaService {

	Conta createOrUpdate(Conta conta);

	Optional<Conta> findById(Long id);

	void delete(Long id);

	List<Conta> findAll();

	Conta deposit(Conta conta, MovementDTO depositDTO);

	Conta withdraw(Conta conta, MovementDTO depositDTO) throws Exception;
}