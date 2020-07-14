package com.vishnu.foodaggregator.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.vishnu.foodaggregator.constants.Constants.*;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("execution(* com.vishnu.foodaggregator.controller..*(..))")
    public void controllerMethods() {
    }

    @Pointcut("execution(* com.vishnu.foodaggregator.service.impl..*(..))")
    public void serviceMethods() {
    }

    @Pointcut("execution(* com.vishnu.foodaggregator.restAdapter.impl..*(..))")
    public void restAdapterMethods() {
    }

    @Around("controllerMethods() || serviceMethods() || restAdapterMethods()")
    public Object logMethod(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();

        String className = methodSignature.getDeclaringType().getSimpleName();
        String methodName = methodSignature.getName();
        String[] parameterNames = methodSignature.getParameterNames();
        Object[] args = proceedingJoinPoint.getArgs();
        String paramList = parameterNames.length == 0 ? "" : populateParamList(parameterNames, args);
        log.info(METHOD_LOGGING_LOGGER_PATTERN_ENTRY,className, methodName, paramList);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Object result = proceedingJoinPoint.proceed();
        stopWatch.stop();

        log.info(METHOD_LOGGING_LOGGER_PATTERN,className, methodName, result, stopWatch.getTotalTimeMillis());
        return result;
    }

    private String populateParamList(String[] parameterNames, Object[] args) {
        return IntStream.range(0, parameterNames.length)
                .mapToObj(i -> parameterNames[i] + " = " + args[i])
                .collect(Collectors.joining(" ,"));
    }
}
