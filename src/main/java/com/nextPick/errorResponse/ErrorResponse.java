package com.nextPick.errorResponse;

import com.nextPick.exception.BusinessLogicException;
import com.nextPick.exception.ExceptionCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;

import javax.validation.ConstraintViolation;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class ErrorResponse {
    private int status;
    private String message;
    private List<ConstraintViolationError> violationErrors;
    private List<FieldError> fieldErrors;

    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public ErrorResponse(List<FieldError> fieldErrors, List<ConstraintViolationError> violationErrors) {
        this.violationErrors = violationErrors;
        this.fieldErrors = fieldErrors;
    }

    public static ErrorResponse of(BindingResult bindingResult) {
        return new ErrorResponse(FieldError.of(bindingResult), null); // 생성자 순서도 맞춰줘야한다.
    }

    public static ErrorResponse of(Set<ConstraintViolation<?>> constraintViolations) {
        return new ErrorResponse(null, ConstraintViolationError.of(constraintViolations));
    }

    public static ErrorResponse of(BusinessLogicException e) {
        return new ErrorResponse(e.getExceptionCode().getStatusCode(), e.getExceptionCode().getStatusDescription());
    }

    public static ErrorResponse of(ExceptionCode exceptionCode) {
        return new ErrorResponse(exceptionCode.getStatusCode(), exceptionCode.getStatusDescription());
    }

    public static ErrorResponse of(HttpStatus httpStatus) {
        return new ErrorResponse(httpStatus.value(), httpStatus.getReasonPhrase());
    }

    public static ErrorResponse of(HttpStatus httpStatus, String message) {
        return new ErrorResponse(httpStatus.value(), message);
    }

    @Getter
    public static class FieldError {
        private String field;
        private Object rejectedValue;
        private String reason;

        public FieldError(String field, Object rejectedValue, String reason) {
            this.field = field;
            this.rejectedValue = rejectedValue;
            this.reason = reason;
        }

        public static List<FieldError> of(BindingResult bindingResult) {
            final List<org.springframework.validation.FieldError> fieldErrors = bindingResult.getFieldErrors();

            return fieldErrors.stream()
                    .map(fieldError -> new FieldError(
                            fieldError.getField(),
                            fieldError.getRejectedValue() == null ? "" : fieldError.getRejectedValue().toString(),
                            fieldError.getDefaultMessage()
                    )).collect(Collectors.toList());
        }
    }

        @Getter
        public static class ConstraintViolationError {
            private String propertyPath;
            private String rejectValue;
            private String reason;

            public ConstraintViolationError(String propertyPath, String rejectValue, String reason) {
                this.propertyPath = propertyPath;
                this.rejectValue = rejectValue;
                this.reason = reason;
            }

            public static List<ConstraintViolationError> of(Set<ConstraintViolation<?>> constraintViolations) {
                return constraintViolations.stream()
                        .map(constraintViolation -> new ConstraintViolationError(
                                constraintViolation.getPropertyPath().toString(),
                                constraintViolation.getInvalidValue().toString(),
                                constraintViolation.getMessage()
                        )).collect(Collectors.toList());
        }
    }
}
