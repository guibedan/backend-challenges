package com.guibedan.notification.service.exceptions;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<String> handleBusinessException(final BusinessException e) {
		log.error(e.getMessage(), e);
		return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(e.getCode()));
	}

}
