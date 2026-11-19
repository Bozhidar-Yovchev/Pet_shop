package com.example.new_final_project.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        HttpSession session = request.getSession(false);
        Object customerId = (session == null) ? null
                : session.getAttribute(AppConstants.SessionKeys.CUSTOMER_ID);

        // Not logged in -> go to the login page
        if (customerId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }

        // Admin area is only for ADMIN
        String adminPrefix = request.getContextPath() + "/admin";
        if (request.getRequestURI().startsWith(adminPrefix)) {
            Object role = session.getAttribute(AppConstants.SessionKeys.ROLE);
            if (!AppConstants.Role.ADMIN.equals(role)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return false;
            }
        }
        return true;
    }
}