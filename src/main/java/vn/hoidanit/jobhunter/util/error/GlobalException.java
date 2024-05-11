package vn.hoidanit.jobhunter.util.error;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import vn.hoidanit.jobhunter.domain.RestRespone;

@RestControllerAdvice
public class GlobalException {
    @ExceptionHandler(value = IdInvalidException.class)
    public ResponseEntity<RestRespone<Object>> handleIdException(IdInvalidException idException) {
        RestRespone<Object> res = new RestRespone<>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError("IdInvalidException");
        res.setMessage(idException.getMessage());
        res.setData(null);
        return ResponseEntity.badRequest().body(res);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<RestRespone<Object>> validationError(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        final List<FieldError> fieldErrors = bindingResult.getFieldErrors();

        RestRespone<Object> res = new RestRespone<>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError(ex.getBody().getDetail());

        List<String> errors = new ArrayList<>();
        for (FieldError fieldError : fieldErrors) {
            errors.add(fieldError.getDefaultMessage());
        }
        res.setMessage(errors.size() > 1 ? errors : errors.get(0));

        return ResponseEntity.badRequest().body(res);

    }

    @ExceptionHandler(value = { InternalAuthenticationServiceException.class, BadCredentialsException.class })
    public ResponseEntity<RestRespone<Object>> AuthServiceException(InternalAuthenticationServiceException ex) {
        RestRespone<Object> res = new RestRespone<>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError(ex.getMessage());
        res.setMessage("Tai khoan khong chinh xac");
        return ResponseEntity.badRequest().body(res);
    }
}
