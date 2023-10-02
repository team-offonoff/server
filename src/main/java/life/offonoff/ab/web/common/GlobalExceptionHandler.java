package life.offonoff.ab.web.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

@Slf4j
// 예외가 발생했을 때 json 형태로 반환할 때 사용하는 어노테이션
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorWrapper> handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.warn("MethodArgumentNotValidException = ", e);

        final String errorFields = e.getBindingResult().getFieldErrors()
                .stream()
                .map(ex -> ex.getDefaultMessage())
                .collect(Collectors.joining(", "));
        final String hint = "잘못된 요청 바디입니다.";
        final ErrorWrapper errors = new ErrorWrapper(
                ErrorContent.of(errorFields, hint, HttpStatus.BAD_REQUEST.value())
        );
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errors);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorWrapper> handleMethodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException e) {
        log.warn("MethodArgumentTypeMismatchException = ", e);

        final String hint = "잘못된 타입의 요청입니다. | 인자 이름: " + e.getName() + " 필요한 타입: " + e.getRequiredType();
        final ErrorWrapper errorWrapper = new ErrorWrapper(
                ErrorContent.of(hint, HttpStatus.BAD_REQUEST.value())
        );
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorWrapper);
    }

}
