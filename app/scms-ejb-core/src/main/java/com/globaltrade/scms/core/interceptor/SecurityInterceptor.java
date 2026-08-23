package com.globaltrade.scms.core.interceptor;

import jakarta.annotation.Resource;
import jakarta.ejb.SessionContext;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;

public class SecurityInterceptor {

    // EJB standard way to get the logged-in user's security context
    @Resource
    private SessionContext sessionContext;

    @AroundInvoke
    public Object checkSecurity(InvocationContext ctx) throws Exception {
        String user = (sessionContext != null && sessionContext.getCallerPrincipal() != null)
                ? sessionContext.getCallerPrincipal().getName()
                : "ANONYMOUS";

        System.out.println("[SECURITY INTERCEPTOR] User '" + user + "' is accessing EJB method: " + ctx.getMethod().getName());

        // Programmatic check example: Block vendors from modifying inventory
        if ("vendor1".equals(user) && ctx.getMethod().getName().equals("deductStock")) {
            System.out.println("[SECURITY INTERCEPTOR] Access Denied: Vendors cannot modify inventory.");
            throw new SecurityException("Vendor account suspended. Access denied.");
        }

        return ctx.proceed();
    }
}