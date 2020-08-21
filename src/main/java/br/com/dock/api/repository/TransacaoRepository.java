package br.com.dock.api.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import br.com.dock.api.entity.Transacao;

public interface TransacaoRepository extends CrudRepository<Transacao, Long> {

	public List<Transacao> findByContaIdConta(Long idConta);

	public List<Transacao> findByContaIdContaAndDataTransacaoBetween(Long idConta, Date dataInicial, Date dataFinal);

}
