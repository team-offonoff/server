package life.offonoff.ab.web.common;

import life.offonoff.ab.exception.AbException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

import static life.offonoff.ab.exception.AbCode.*;

@Slf4j
// 예외가 발생했을 때 json 형태로 반환할 때 사용하는 어노테이션
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AbException.class)
    private ResponseEntity<ErrorWrapper> handleAbException(final AbException abException) {
        log.warn("AbException = ", abException);

        final ErrorWrapper error = new ErrorWrapper(
                abException.getAbCode(),
                ErrorContent.of(abException.getMessage(), abException.getHint(), abException.getHttpStatusCode(), abException.getPayload())
        );
        return ResponseEntity
                .status(HttpStatus.valueOf(abException.getHttpStatusCode()))
                .body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<ErrorWrapper> handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.warn("MethodArgumentNotValidException = ", e);

        final String errorFields = e.getBindingResult().getFieldErrors()
                .stream()
                .map(ex -> ex.getDefaultMessage())
                .collect(Collectors.joining(", "));
        final String message = "잘못된 요청 바디입니다.";
        final ErrorWrapper errors = new ErrorWrapper(
                INVALID_FIELD,
                ErrorContent.of(message, errorFields, HttpStatus.BAD_REQUEST.value())
        );
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errors);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    private ResponseEntity<ErrorWrapper> handleMethodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException e) {
        log.warn("MethodArgumentTypeMismatchException = ", e);

        final String message = "잘못된 타입의 요청입니다. | 인자 이름: " + e.getName() + " 필요한 타입: " + e.getRequiredType();
        final ErrorWrapper errorWrapper = new ErrorWrapper(
                INVALID_FIELD,
                ErrorContent.of(message, HttpStatus.BAD_REQUEST.value())
        );
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorWrapper);
    }

    // Request DTO 생성 중 exception 발생한 경우
    @ExceptionHandler(HttpMessageNotReadableException.class)
    private ResponseEntity<ErrorWrapper> handleHttpMessageNotReadableException(final HttpMessageNotReadableException e) {
        final Throwable cause = e.getCause().getCause();
        if (cause instanceof AbException) {
            return handleAbException((AbException) cause);
        }
        return handleException(e);
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    private ResponseEntity<ErrorWrapper> handleOptimisticLockingFailureException(
            final ObjectOptimisticLockingFailureException e) {
        log.warn("Unhandled Optimistic Lock! = ", e);
        final String message = "서버 문제가 발생했습니다. 다시 시도해주세요.";
        final ErrorWrapper errorWrapper = new ErrorWrapper(
                CONCURRENCY_VIOLATION,
                ErrorContent.of(message, HttpStatus.CONFLICT.value())
        );
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(errorWrapper);
    }

    @ExceptionHandler(Exception.class)
    private ResponseEntity<ErrorWrapper> handleException(final Exception exception) {
        log.warn("! Unfiltered Exception = ", exception);

        final ErrorWrapper errorWrapper = new ErrorWrapper(
                INTERNAL_SERVER_ERROR,
                ErrorContent.of(exception.getMessage(), HttpStatus.BAD_REQUEST.value())
        );
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorWrapper);
    }

}
