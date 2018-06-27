package top.aceofspades.blog.advice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import top.aceofspades.blog.util.ConstraintViolationExceptionHandler;
import top.aceofspades.blog.vo.Response;

import javax.validation.ConstraintViolationException;

/**
 * Controller统一异常处理
 * @author ace
 * @version 1.0
 * @since 2018/6/28 2:00
 */
@ControllerAdvice(basePackages = "top.aceofspades.blog.controller")
public class ExceptionHandlerAdvice {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

    /*此方法返回值，可以是Controller可以返回的任何值*/
    /*全局异常处理*/
    @ExceptionHandler(Exception.class)
    public Object handleException(Exception e, WebRequest request) {
        String requestType = request.getHeader("X-Requested-With");
        if ("XMLHttpRequest".equals(requestType)) {//Ajax请求
            if (e instanceof ConstraintViolationException) {
                return ResponseEntity.ok().body(new Response(false,
                        ConstraintViolationExceptionHandler.getMessage((ConstraintViolationException) e)));
            } else if (e instanceof TransactionSystemException) {
                Throwable t = e.getCause();
                while ((t != null) && !(t instanceof ConstraintViolationException)) {
                    t = t.getCause();
                }
                if (t != null) {
                    return ResponseEntity.ok().body(
                            new Response(false, ConstraintViolationExceptionHandler.getMessage((ConstraintViolationException) t)));
                }
            }else if(e instanceof DataIntegrityViolationException){
                return ResponseEntity.ok().body(
                        new Response(false, "账号或邮箱重复"));
            }
            logger.error("【系统异常】={}", e.getMessage());
            return ResponseEntity.ok().body(
                    new Response(false, parseException(e).getMessage()));

        } else {//非Ajax请求
            //此时requestType为null
            logger.error("【系统异常】={}", e.getMessage());
            ModelAndView modelAndView = new ModelAndView("error");
            modelAndView.addObject("errorMsg", e.getMessage());
            return modelAndView;
        }
    }

    //获得最内层异常
    private static Throwable parseException(Throwable e) {
        Throwable tmp = e;
        int breakPoint = 0;
        while (tmp.getCause() != null) {
            if (tmp.equals(tmp.getCause())) {
                break;
            }
            tmp = tmp.getCause();
            breakPoint++;
            if (breakPoint > 1000) {
                break;
            }
        }
        return tmp;
    }
}
