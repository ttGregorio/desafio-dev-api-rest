package br.com.dock.api.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

@Data
public class MovementDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long idConta;

	private BigDecimal value;
}
