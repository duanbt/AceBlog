package top.aceofspades.blog.listener.event;

import top.aceofspades.blog.domain.Blog;

/**
 * 保存博客 事件.
 *
 * @author ace
 * @version 1.0
 * @since 2018/6/16 14:30
 */
public class BlogSaveEvent extends BlogEvent {


    private Blog blog;

    /**
     * 是否是新增 新增为true,更新为false
     */
    private boolean isAdd;

    public Blog getBlog() {
        return blog;
    }

    public boolean isAdd() {
        return isAdd;
    }

    public BlogSaveEvent(Object source, Blog blog, boolean isNew) {
        super(source);
        this.blog = blog;
        this.isAdd = isNew;
    }
}
