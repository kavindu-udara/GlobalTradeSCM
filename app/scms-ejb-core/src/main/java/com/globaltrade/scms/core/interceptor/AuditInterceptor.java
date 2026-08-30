package com.globaltrade.scms.core.interceptor;

import com.globaltrade.scms.api.interceptor.LogisticsAudit;
import com.globaltrade.scms.core.audit.AuditServiceBean;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

@Interceptor
@LogisticsAudit
public class AuditInterceptor {

    @Inject
    private AuditServiceBean auditService;

    @AroundInvoke
    public Object audit(InvocationContext ctx) throws Exception {
        String className = ctx.getTarget().getClass().getSimpleName();
        String methodName = ctx.getMethod().getName();

        // Read the custom annotation from the method or class
        LogisticsAudit annotation = ctx.getMethod().getAnnotation(LogisticsAudit.class);
        if (annotation == null) {
            annotation = ctx.getTarget().getClass().getAnnotation(LogisticsAudit.class);
        }

        String module = (annotation != null) ? annotation.module() : "UNKNOWN";
        boolean compliance = (annotation != null) && annotation.requiresComplianceCheck();

        System.out.println("[AOP AUDIT] Intercepting " + className + "." + methodName + " | Module: " + module + " | Compliance: " + compliance);

        String outcome = "SUCCESS";
        try {
            return ctx.proceed();
        } catch (Exception e) {
            outcome = "FAILURE";
            throw e;
        } finally {
            auditService.saveAuditLog("system_user", methodName, module, outcome);
        }
    }
}