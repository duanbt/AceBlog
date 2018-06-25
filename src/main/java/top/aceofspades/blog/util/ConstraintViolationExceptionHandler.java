package top.aceofspades.blog.util;

import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

/**
 * ConstraintViolationException 处理器.
 * @author ace
 * @version 1.0
 * @since 2018/6/3 19:31
 */
public class ConstraintViolationExceptionHandler {

    /**
     * 获取批量异常信息
     * @param e ConstraintViolationException异常对象
     * @return 以 ';' 分割的异常信息
     */
    public static String getMessage(ConstraintViolationException e){
        List<String> msgList = new ArrayList<>();
        for (ConstraintViolation<?> constraintViolation : e.getConstraintViolations()) {
            msgList.add(constraintViolation.getMessage());
        }
        return StringUtils.join(msgList.toArray(), ";");
    }
}
