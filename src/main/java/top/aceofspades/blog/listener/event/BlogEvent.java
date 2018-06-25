package top.aceofspades.blog.listener.event;

import org.springframework.context.ApplicationEvent;

/**
 * blog event.
 * @author ace
 * @version 1.0
 * @since 2018/6/16 14:40
 */
public abstract class BlogEvent  extends ApplicationEvent {

    public BlogEvent(Object source) {
        super(source);
    }
}
