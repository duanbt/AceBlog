package top.aceofspades.blog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import top.aceofspades.blog.domain.Blog;
import top.aceofspades.blog.domain.Catalog;
import top.aceofspades.blog.domain.User;

/**
 * Blog 实体.
 * @author ace
 * @version 1.0
 * @since 2018/6/6 20:14
 */
public interface BlogRepository extends JpaRepository<Blog, Long> {

    /**
     * 根据用户 && (博客标题 || 博客标签) 分页查询博客列表
     * @param user
     * @param title
     * @param user2
     * @param tag
     * @param pageable
     * @return
     */
    Page<Blog> findByUserAndTitleLikeOrUserAndTagsLike(User user, String title, User user2, String tag, Pageable pageable);

    /**
     * 根据用户&&博客分类 分页查询博客列表
     * @param user
     * @param catalog
     * @param pageable
     * @return
     */
    Page<Blog> findByUserAndCatalog(User user, Catalog catalog, Pageable pageable);

    /**
     * 根据用户&&标签 分页查询博客
     * @param user
     * @param tag
     * @param pageable
     * @return
     */
    Page<Blog> findByUserAndTagsLike(User user, String tag, Pageable pageable);

    /**
     * 根据用户 或 标题 分页查询博客
     * @param user
     * @param title
     * @param pageable
     * @return
     */
    Page<Blog> findByUserOrTitleLike(User user, String title, Pageable pageable);
}
