package ch.admin.seco.jobroom.service.impl.security;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
class TechnicalUserContextAspect {

    @Pointcut("@annotation(ch.admin.seco.jobroom.service.impl.security.LoginAsTechnicalUser)")
    void loginAsTechnicalUserPointcut() {
    }

    @Around("loginAsTechnicalUserPointcut() ")
    Object initSecurityContextAround(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            TechnicalUserContextUtil.initContext();
            return joinPoint.proceed();
        } finally {
            TechnicalUserContextUtil.clearContext();
        }
    }
}
