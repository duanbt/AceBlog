package top.aceofspades.blog.util;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author ace
 * @version 1.0
 * @since 2018/6/7 22:06
 */
public class UpdateUtil {
    /**
     * 除过目标源中不为空的字段，将数据库中查出的数据源复制到要提交的目标源中
     *
     * @param source 用id从数据库中查出来的数据源
     * @param target 提交的实体，目标源
     */
    public static void coverNullProperties(Object source, Object target) {
        BeanUtils.copyProperties(source, target, getNoNullProperties(target));
    }

    /**
     * @param target 目标源数据
     * @return 将目标源中不为空和不为零的字段取出
     */
    private static String[] getNoNullProperties(Object target) {
        BeanWrapper srcBean = new BeanWrapperImpl(target);
        PropertyDescriptor[] pds = srcBean.getPropertyDescriptors();
        Set<String> noEmptyName = new HashSet<>();
        for (PropertyDescriptor p : pds) {
            Object value = srcBean.getPropertyValue(p.getName());
            if (value != null && !value.equals(0)) noEmptyName.add(p.getName());
        }
        String[] result = new String[noEmptyName.size()];
        result =  noEmptyName.toArray(result);
        return result;
    }
}

