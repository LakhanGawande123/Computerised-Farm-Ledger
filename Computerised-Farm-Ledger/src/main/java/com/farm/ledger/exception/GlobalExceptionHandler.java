package com.farm.ledger.exception;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.farm.ledger.constant.FarmLedgerExceptionConstants;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
	
	@Autowired
	private MessageSource errorMessage;
	
	@Autowired
	private MessageSource httpErrorCode;
	
	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

//    @ExceptionHandler(ResourceNotFoundException.class)
//    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
//        ErrorResponse err = ErrorResponse.builder()
//                .timestamp(LocalDateTime.now())
//                .status(HttpStatus.NOT_FOUND.value())
//                .error("Resource Not Found")
//                .message(ex.getMessage())
//                .path(req.getRequestURI())
//                .build();
//        return new ResponseEntity<>(err, HttpStatus.NOT_FOUND);
//    }
//
//    @ExceptionHandler(BadRequestException.class)
//    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex, HttpServletRequest req) {
//        ErrorResponse err = ErrorResponse.builder()
//                .timestamp(LocalDateTime.now())
//                .status(HttpStatus.BAD_REQUEST.value())
//                .error("Bad Request")
//                .message(ex.getMessage())
//                .path(req.getRequestURI())
//                .build();
//        return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
//        String errors = ex.getBindingResult().getFieldErrors().stream()
//                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
//                .collect(Collectors.joining("; "));
//        ErrorResponse err = ErrorResponse.builder()
//                .timestamp(LocalDateTime.now())
//                .status(HttpStatus.BAD_REQUEST.value())
//                .error("Validation Error")
//                .message(errors)
//                .path(req.getRequestURI())
//                .build();
//        return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
//    }
//    
//    
//
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ErrorResponse> handleAll(Exception ex, HttpServletRequest req) {
//        ex.printStackTrace(); // replace with logger in prod
//        ErrorResponse err = ErrorResponse.builder()
//                .timestamp(LocalDateTime.now())
//                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
//                .error("Internal Server Error")
//                .message(ex.getMessage())
//                .path(req.getRequestURI())
//                .build();
//        return new ResponseEntity<>(err, HttpStatus.INTERNAL_SERVER_ERROR);
//    }
    
    // New code
    
    @ExceptionHandler (FarmLedgerServiceException.class)
    public final ResponseEntity<ErrorResponse> handleSearchAndReportServiceException(
    		FarmLedgerServiceException exception) {
        final ErrorResponse errorResponse = new ErrorResponse();
        final int httpCode = createErrorResponse (errorResponse, exception.getCode(), exception.getArrayOfValues(), null);
        //logErrorMessage (exception, errorResponse);
        return new ResponseEntity<> (errorResponse, HttpStatus.valueOf(httpCode));
    }

    @ExceptionHandler (value = { ConstraintViolationException.class })
    public ResponseEntity<ErrorResponse> constraintViolationException (ConstraintViolationException exception) {
        final Set<ConstraintViolation<?>> violations = exception.getConstraintViolations();
        final ErrorResponse errorResponse = new ErrorResponse();
        final StringBuilder exceptionMessages = new StringBuilder();
        for (final ConstraintViolation<?> violation: violations) {
            exceptionMessages.append(violation.getMessageTemplate());
        }
        errorResponse.setErrorCode (FarmLedgerExceptionConstants.EXCEPTION_CONSTRAINT_VIOLATION);
        errorResponse.setErrorMessage (exceptionMessages.toString());
        logErrorMessage (exception, errorResponse);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
//    @Override
//    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
//        final ErrorResponse errorResponse = new ErrorResponse();
//           String path = ((ServletWebRequest) request).getRequest().getRequestURI();
//        try {
//            String errorCode = null;
//            if (ex.getBindingResult() != null && !ex.getBindingResult().getFieldErrors().isEmpty()) {
//                errorCode = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
//            }
//
//            if (path.startsWith("/api")) {
//                if (errorCode != null) {
//                    errorResponse.setErrorCode(errorCode);
//                    errorResponse.setErrorMessage(errorMessage.getMessage(errorCode, new String[] {}, Locale.ENGLISH));
//                }
//            } else {
//                final List<Validation> validationsList = new ArrayList<>();
//                ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
//                    final String[] fieldName = fieldError.getField().split("\\.");
//                    final Validation validation = new Validation();
//                    validation.setField(fieldName[fieldName.length - 1]);
//                    validation.setErrorMessage(errorMessage.getMessage(fieldError.getDefaultMessage(), null, null));
//                    validationsList.add(validation);
//                });
//                errorResponse.setValidation(validationsList);
//                errorResponse.setErrorCode(errorCode);
//            }
//        } catch (Exception e) {
//            errorResponse.setErrorCode(FarmLedgerExceptionConstants.EXCEPTION_UNEXPECTED);
//            errorResponse.setErrorMessage(errorMessage.getMessage(
//            		FarmLedgerExceptionConstants.EXCEPTION_UNEXPECTED, null,
//                Locale.getDefault()));
//        }
//        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
//    }

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String errorCode;
        ErrorResponse errorResponse = new ErrorResponse();

        try {
            errorCode = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
            if (errorCode != null) {
                errorResponse.setErrorMessage(errorMessage.getMessage(errorCode, new String[] {}, Locale.ENGLISH));
                errorResponse.setErrorCode(errorCode);
            }

        } catch (Exception e) {
            errorResponse.setErrorMessage(errorMessage.getMessage(
                    FarmLedgerExceptionConstants.EXCEPTION_UNEXPECTED, new String[] {}, Locale.ENGLISH));
            errorResponse.setErrorCode(FarmLedgerExceptionConstants.EXCEPTION_UNEXPECTED);
        }
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ErrorResponse> handleErrorResponse(Exception exception) {
        final ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode(FarmLedgerExceptionConstants.EXCEPTION_SERVER_ERROR);
        errorResponse.setErrorMessage(errorMessage.getMessage(FarmLedgerExceptionConstants.EXCEPTION_SERVER_ERROR,
                new Object[] { "" }, null, Locale.getDefault()));
        logErrorMessage(exception, errorResponse);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException exception,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        final ErrorResponse errorResponse = new ErrorResponse();
        final int httpCode = createErrorResponse(errorResponse,
        		FarmLedgerExceptionConstants.EXCEPTION_UNSUPPORTED_MEDIA_TYPE, null, null);
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(httpCode));
    }

    public int createErrorResponse(ErrorResponse errorResponse, String code, String[] arrayOfValues, List<Validation> validationList) {
        final String httpErrorCodeMessage = httpErrorCode.getMessage(code, null, Locale.getDefault());
        final int httpErrorCodeInteger = Integer.parseInt(httpErrorCodeMessage);
        errorResponse.setErrorCode(code);
        errorResponse.setErrorMessage(populateErrorMessageField(code, arrayOfValues));
        if (validationList != null) {
            errorResponse.setValidation(validationList);
        }
        return httpErrorCodeInteger;
    } 
    
    private String populateErrorMessageField(String code, String... arrayOfValues) {
        return errorMessage.getMessage(code, arrayOfValues, Locale.getDefault());
    }

    public ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException noHandlerFoundException, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        final ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode(FarmLedgerExceptionConstants.EXCEPTION_METHOD_NOT_FOUND);
        errorResponse.setErrorMessage(errorMessage.getMessage(FarmLedgerExceptionConstants.EXCEPTION_METHOD_NOT_FOUND, new Object[] {""}, Locale.getDefault()));
        return new ResponseEntity<>(errorResponse, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Override
    public ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        final ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode(FarmLedgerExceptionConstants.EXCEPTION_METHOD_NOT_FOUND);
        errorResponse.setErrorMessage(errorMessage.getMessage(FarmLedgerExceptionConstants.EXCEPTION_METHOD_NOT_FOUND, 
        		new Object[] {""}, Locale.getDefault()));
        return new ResponseEntity<>(errorResponse, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Override
    public ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        final ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode(FarmLedgerExceptionConstants.EXCEPTION_QUERY_PARAM_EMPTY);
        errorResponse.setErrorMessage(ex.getMessage());
        // LogErrorMessage(ex, errorResponse); // Assuming this is a comment or method call
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    private static void logErrorMessage(Exception exception, ErrorResponse errorResponse) {
    	logger.error(errorResponse.getErrorMessage(), exception);
    }
    
    
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException exception,
    		HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    	Throwable mostSpecificCause = exception.getMostSpecificCause();
    	com.farm.ledger.exception.ErrorResponse errorResponse = new com.farm.ledger.exception.ErrorResponse();
    	ResponseEntity<Object> responseEntity;
    	FarmLedgerServiceException globalException;
    	if (mostSpecificCause instanceof FarmLedgerServiceException) {
			globalException = (FarmLedgerServiceException) mostSpecificCause;
			int httpCode = createErrorResponseBean(errorResponse, globalException.getCode(), globalException.getArrayOfValues());
			
			responseEntity = new ResponseEntity<>(errorResponse, HttpStatus.valueOf(httpCode));
			
		} else {
			responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
    	
    	return super.handleHttpMessageNotReadable(exception, headers, status, request);
    }
    
    public int createErrorResponseBean(com.farm.ledger.exception.ErrorResponse errorResponse, String code, String... arrayOfValues) {
    	String httpErrorCodeMessage = httpErrorCode.getMessage(code, null, Locale.getDefault());
    	int httpErrorCode = Integer.parseInt(httpErrorCodeMessage.trim());
    	errorResponse.setErrorCode(code);
    	errorResponse.setErrorMessage(errorMessage.getMessage(code, arrayOfValues, Locale.getDefault()));
    	return httpErrorCode;
    }
}
