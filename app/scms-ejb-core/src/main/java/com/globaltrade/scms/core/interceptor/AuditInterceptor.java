package com.globaltrade.scms.core.interceptor;

import com.globaltrade.scms.core.audit.AuditServiceBean;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;

public class AuditInterceptor {

    @Inject
    private AuditServiceBean auditService;

    @AroundInvoke
    public Object audit(InvocationContext ctx) throws Exception {
        String className = ctx.getTarget().getClass().getSimpleName();
        String methodName = ctx.getMethod().getName();

        String outcome = "SUCCESS";
        try {
            System.out.println("[AUDIT INTERCEPTOR] Entering: " + className + "." + methodName);
            return ctx.proceed(); // Execute the actual business method
        } catch (Exception e) {
            outcome = "FAILURE";
            throw e; // Re-throw so the application handles the error normally
        } finally {
            // Save audit log in a separate REQUIRES_NEW transaction
            auditService.saveAuditLog("system_user", methodName, className, outcome);
        }
    }
}
