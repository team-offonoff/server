package life.offonoff.ab.web.common.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

@Component
@Aspect
public class LoggingAspectHandler {
    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());
    @Pointcut("execution(* life.offonoff.ab.web.*.*(..))")
    private void allControllers() {
    }

    @Around("allControllers()")
    public Object doReturnLogging(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        Object[] arguments = joinPoint.getArgs();

        log.info("[BEFORE] METHOD={} PARAMETER={} WILL EXECUTE", methodName, arguments);

        Object returnedByMethod = joinPoint.proceed(arguments);

        log.info("[RETURNED] METHOD={} PARAMETER={} RETURNED={}", joinPoint.getSignature().toShortString(), joinPoint.getArgs(), returnedByMethod);

        return returnedByMethod;
    }

    @AfterThrowing(value = "allControllers()", throwing = "exception")
    public void doExceptionLogging(JoinPoint joinPoint, Exception exception) {
        String ip = Optional.ofNullable((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .map(attributes -> attributes.getRequest().getHeader("X-Real-IP"))
                .orElse("");
        log.error("[EXCEPTION THROWN] METHOD={} PARAMETER={} THREW EXCEPTION={} | ip={}",
                  joinPoint.getSignature().toShortString(), joinPoint.getArgs(), exception, ip);
    }
}
