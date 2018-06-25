package top.aceofspades.blog.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import top.aceofspades.blog.service.InitAceBlogService;

/**
 * 初始化博客监听器.
 * 监听Spring容器启动完成事件，初始化权限、初始化管理员
 *
 * @author ace
 * @version 1.0
 * @since 2018/6/20 12:18
 */

@Component
public class InitAceBlogListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private InitAceBlogService initAceBlogService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        initAceBlogService.initAuthority();
        initAceBlogService.initAdmin();
    }
}
