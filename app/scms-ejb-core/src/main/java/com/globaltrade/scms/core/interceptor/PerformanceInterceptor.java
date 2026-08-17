package com.globaltrade.scms.core.interceptor;

import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;

public class PerformanceInterceptor {

    @AroundInvoke
    public Object monitorPerformance(InvocationContext ctx) throws Exception {
        long startTime = System.currentTimeMillis();
        String methodName = ctx.getMethod().getName();

        try {
            return ctx.proceed();
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            System.out.println("[PERFORMANCE INTERCEPTOR] Method: " + methodName + " | Execution Time: " + duration + " ms");
        }
    }
}