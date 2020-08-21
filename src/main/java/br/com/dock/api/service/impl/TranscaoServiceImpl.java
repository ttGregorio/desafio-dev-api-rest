package br.com.dock.api.service.impl;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.dock.api.dto.TransacaoSearchDTO;
import br.com.dock.api.entity.Transacao;
import br.com.dock.api.repository.TransacaoRepository;
import br.com.dock.api.service.TransacaoService;

@Service
public class TranscaoServiceImpl implements TransacaoService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private TransacaoRepository transacaoRepository;

	@Override
	public Transacao createOrUpdate(Transacao transacao) {
		logger.info("[TransacaoService][createOrUpdate][transacao: {}]", transacao.toString());
		return transacaoRepository.save(transacao);
	}

	@Override
	public Optional<Transacao> findById(Long id) {
		logger.info("[TransacaoService][findById][id: {}]", id);
		return transacaoRepository.findById(id);
	}

	@Override
	public void delete(Long id) {
		logger.info("[TransacaoService][delete][id: {}]", id);
		transacaoRepository.deleteById(id);
	}

	@Override
	public List<Transacao> findAll(Long idConta) {
		logger.info("[TransacaoService][findAll][idConta: {}]", idConta);
		return transacaoRepository.findByContaIdConta(idConta);
	}

	@Override
	public List<Transacao> search(TransacaoSearchDTO transacao) {
		logger.info("[TransacaoService][findAll][transacao: {}]", transacao.toString());
		return transacaoRepository.findByContaIdContaAndDataTransacaoBetween(transacao.getIdConta(),
				transacao.getDataInicial(), transacao.getDataFinal());
	}
}
