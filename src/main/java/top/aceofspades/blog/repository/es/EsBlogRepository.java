package top.aceofspades.blog.repository.es;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import top.aceofspades.blog.domain.es.EsBlog;

import java.util.List;

/**
 * EsBlog Repository接口.
 *
 * @author ace
 * @version 1.0
 * @since 2018/6/15 19:52
 */
public interface EsBlogRepository extends ElasticsearchRepository<EsBlog, String> {

    /**
     * 模糊查询
     * @param title
     * @param summary
     * @param content
     * @param tag
     * @param pageable
     * @return
     */
    Page<EsBlog> findByTitleContainingOrSummaryContainingOrContentContainingOrTagsContaining(String title, String summary, String content, String tag, Pageable pageable);


    /**
     * 根据 Blog 的 id 查询 EsBlog
     * @param blogId
     * @return
     */
    EsBlog findByBlogId(Long blogId);


    /**
     * 根据标签模糊查询
     * @param tag
     * @param pageable
     * @return
     */
    Page<EsBlog> findByTagsContaining(String tag, Pageable pageable);

}
