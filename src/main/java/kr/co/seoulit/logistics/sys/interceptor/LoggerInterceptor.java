package kr.co.seoulit.logistics.sys.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoggerInterceptor implements HandlerInterceptor {
   private final Log log = LogFactory.getLog(getClass());

   @Override
   public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
      log.debug("======================================          START         ======================================");
      log.debug(" Request URI \t:  " + request.getRequestURI());
      return true;
   }

   @Override
   public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
      log.debug("======================================           END          ======================================\n");
   }

   @Override
   public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
      if (ex != null) {
         log.debug("======================================           END          ======================================\n");
      }
   }
}
