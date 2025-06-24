package com.guibedan.investment.aggregator.exceptions;

import lombok.Getter;
import lombok.Setter;

public class BusinessException extends RuntimeException {

	@Getter
	@Setter
	private int code;

	public BusinessException(String message, int code) {
		super(message);
		this.code = code;
	}

}
