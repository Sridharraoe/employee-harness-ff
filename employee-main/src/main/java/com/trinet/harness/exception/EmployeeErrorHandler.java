package com.trinet.harness.exception;

import java.time.LocalDate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
public class EmployeeErrorHandler extends ResponseEntityExceptionHandler {
	Logger employeeErrorHandlerLogger = LoggerFactory.getLogger(EmployeeErrorHandler.class);
	@ExceptionHandler({ RuntimeException.class })
	public ResponseEntity<ErrorResponse> currencyNotFoundException(Exception ex, WebRequest request) {
		ErrorResponse errorResponse = new ErrorResponse(LocalDate.now(), ex.getMessage(),
				request.getDescription(false));
//		employeeErrorHandlerLogger.info("Exception: {}",errorResponse);	
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {

		String errorMessage = ex.getBindingResult().getFieldErrors().stream()
				.map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
				.collect(Collectors.joining("; "));

		ErrorResponse errorResponse = new ErrorResponse(LocalDate.now(), errorMessage,
		request.getDescription(false));
		
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}
}
