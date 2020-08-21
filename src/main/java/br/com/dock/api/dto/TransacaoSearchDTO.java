package br.com.dock.api.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class TransacaoSearchDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Date dataInicial;
	
	private Date dataFinal;
	
	private Long idConta;
}
