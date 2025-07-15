package com.underrRndezvous.backend.interceptor;

import com.underrRndezvous.backend.config.ConstValue;
import com.underrRndezvous.backend.exception.NotExistSessionException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class LoginCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();

        if(session == null || session.getAttribute(ConstValue.sessionName )==null) {
            log.info("request url : {} {}",request.getRequestURI(),request.getSession().getAttribute(ConstValue.sessionName));
            throw new NotExistSessionException();
        }

        return true;
    }
}
