package com.globaltrade.scms.core.interceptor;

import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;

public class SecurityInterceptor {

    @Context
    private SecurityContext securityContext;

    @AroundInvoke
    public Object checkSecurity(InvocationContext ctx) throws Exception {
        String user = (securityContext != null && securityContext.getUserPrincipal() != null)
                ? securityContext.getUserPrincipal().getName()
                : "ANONYMOUS";

        System.out.println("[SECURITY INTERCEPTOR] User '" + user + "' is accessing " + ctx.getMethod().getName());

        // Programmatic check example: Block suspended vendors
        if ("vendor1".equals(user) && ctx.getMethod().getName().contains("update")) {
            System.out.println("[SECURITY INTERCEPTOR] Access Denied: Vendor account is suspended.");
            throw new SecurityException("Vendor account suspended. Access denied.");
        }

        return ctx.proceed();
    }
}