package top.aceofspades.blog.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import top.aceofspades.blog.domain.Blog;
import top.aceofspades.blog.domain.es.EsBlog;
import top.aceofspades.blog.listener.event.BlogEvent;
import top.aceofspades.blog.listener.event.BlogRemoveEvent;
import top.aceofspades.blog.listener.event.BlogSaveEvent;
import top.aceofspades.blog.service.EsBlogService;

/**
 * 同步EsBlog的 监听器.
 *
 * @author ace
 * @version 1.0
 * @since 2018/6/16 14:35
 */
@Component
public class EsBlogListener implements ApplicationListener<BlogEvent> {

    @Autowired
    private EsBlogService esBlogService;

    @Override
    public void onApplicationEvent(BlogEvent event) {
        if (event instanceof BlogSaveEvent) {
            BlogSaveEvent blogSaveEvent = (BlogSaveEvent) event;
            this.updateEsBlog(blogSaveEvent.getBlog(), blogSaveEvent.isAdd());
        } else if (event instanceof BlogRemoveEvent) {
            BlogRemoveEvent blogRemoveEvent = (BlogRemoveEvent) event;
            this.removeEsBlog(blogRemoveEvent.getBlogId());
        }
    }

    private void removeEsBlog(Long blogId) {
        EsBlog esBlog = esBlogService.getEsBlogByBlogId(blogId);
        esBlogService.removeEsBlog(esBlog.getId());
    }

    private void updateEsBlog(Blog blog, boolean isNew) {
        if (isNew) {
            esBlogService.updateEsBlog(new EsBlog(blog));
        }else {
            EsBlog esBlog = esBlogService.getEsBlogByBlogId(blog.getId());
            esBlog.update(blog);
            esBlogService.updateEsBlog(esBlog);
        }
    }
}
