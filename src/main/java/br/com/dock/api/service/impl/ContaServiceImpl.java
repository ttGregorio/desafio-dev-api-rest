package br.com.dock.api.service.impl;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.dock.api.dto.MovementDTO;
import br.com.dock.api.entity.Conta;
import br.com.dock.api.entity.Transacao;
import br.com.dock.api.repository.ContaRepository;
import br.com.dock.api.repository.TransacaoRepository;
import br.com.dock.api.service.ContaService;

@Service
public class ContaServiceImpl implements ContaService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ContaRepository contaRepository;

	@Autowired
	private TransacaoRepository transacaoRepository;

	@Override
	public Conta createOrUpdate(Conta conta) {
		logger.info("[ContaService][createOrUpdate][conta: {}]", conta.toString());
		return contaRepository.save(conta);
	}

	@Override
	public Optional<Conta> findById(Long id) {
		logger.info("[ContaService][findById][id: {}]", id);
		return contaRepository.findById(id);
	}

	@Override
	public void delete(Long id) {
		logger.info("[ContaService][delete][id: {}]", id);
		contaRepository.deleteById(id);
	}

	@Override
	public List<Conta> findAll() {
		logger.info("[ContaService][findAll]");
		return contaRepository.findAll();
	}

	@Override
	public Conta deposit(Conta conta, MovementDTO depositDTO) {
		logger.info("[ContaService][deposit][conta: {}][depositDTO: {}]", conta.toString(), depositDTO);

		conta.setSaldo(conta.getSaldo().add(depositDTO.getValue()));
		return contaRepository.save(conta);
	}

	@Override
	public Conta withdraw(Conta conta, MovementDTO depositDTO) throws Exception {
		logger.info("[ContaService][withdraw][conta: {}][depositDTO: {}]", conta.toString(), depositDTO);
		Calendar calendar = new GregorianCalendar();
		calendar.set(Calendar.HOUR, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		calendar.add(Calendar.DATE, -1);

		Date dataInicial = calendar.getTime();

		calendar.set(Calendar.HOUR, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);

		Date dataFinal = calendar.getTime();
		List<Transacao> transacoes = transacaoRepository.findByContaIdContaAndDataTransacaoBetween(conta.getIdConta(),
				dataInicial, dataFinal);

		BigDecimal totalSaquesDia = new BigDecimal(0);

		for (Transacao transacao : transacoes) {
			totalSaquesDia = totalSaquesDia.add(transacao.getValor());
		}

		totalSaquesDia = totalSaquesDia.add(depositDTO.getValue());

		if (totalSaquesDia.compareTo(conta.getLimiteSaqueDiario()) > 0) {
			throw new Exception("Limite de saque diário atingido");
		}

		Transacao transacao = new Transacao(null, conta, depositDTO.getValue(), new Date());

		transacaoRepository.save(transacao);

		conta.setSaldo(conta.getSaldo().subtract(depositDTO.getValue()));
		return contaRepository.save(conta);
	}
}
