package br.com.dock.api.service.impl;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.dock.api.entity.Pessoa;
import br.com.dock.api.repository.PessoaRepository;
import br.com.dock.api.service.PessoaService;

@Service
public class PessoaServiceImpl implements PessoaService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PessoaRepository pessoaRepository;

	@Override
	public Pessoa createOrUpdate(Pessoa pessoa) {
		logger.info("[PessoaService][createOrUpdate][pessoa: {}]", pessoa.toString());
		return pessoaRepository.save(pessoa);
	}

	@Override
	public Optional<Pessoa> findById(Long id) {
		logger.info("[PessoaService][findById][id: {}]", id);
		return pessoaRepository.findById(id);
	}

	@Override
	public void delete(Long id) {
		logger.info("[PessoaService][delete][id: {}]", id);
		pessoaRepository.deleteById(id);
	}

	@Override
	public List<Pessoa> findAll() {
		logger.info("[PessoaService][findAll]");
		return pessoaRepository.findAll();
	}
}
