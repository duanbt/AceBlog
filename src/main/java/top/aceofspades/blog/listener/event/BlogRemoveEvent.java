package top.aceofspades.blog.listener.event;

/**
 * 删除博客 事件.
 * @author ace
 * @version 1.0
 * @since 2018/6/16 14:47
 */
public class BlogRemoveEvent extends BlogEvent {

    private Long blogId;

    public Long getBlogId() {
        return blogId;
    }

    public BlogRemoveEvent(Object source, Long blogId) {
        super(source);
        this.blogId = blogId;
    }
}
