package top.aceofspades.blog.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import top.aceofspades.blog.domain.*;

import java.util.List;
import java.util.Optional;

/**
 * Blog 服务接口
 *
 * @author ace
 * @version 1.0
 * @since 2018/6/6 20:21
 */
public interface BlogService {

    /**
     * 保存Blog
     *
     * @param blog
     * @return
     */
    Blog saveBlog(Blog blog);

    /**
     * 删除Blog
     *
     * @param id
     */
    void removeBlog(Long id);

    /**
     * 根据Id获得Blog
     *
     * @param id
     * @return
     */
    Optional<Blog> getBlogById(Long id);

    /**
     * 根据用户 进行 关键字（title or tag） 分页模糊查询（最新：按照创建时间）
     *
     * @param user
     * @param keyword
     * @param pageable
     * @return
     */
    Page<Blog> listBlogsByUserAndKeywordByTime(User user, String keyword, Pageable pageable);


    /**
     * 根据用户  进行 关键字（title or tag） 分页模糊查询（最热:综合阅读量、点赞数）
     *
     * @param user
     * @param keyword
     * @param pageable
     * @return
     */
    Page<Blog> listBlogsByUserAndKeywordByHot(User user, String keyword, Pageable pageable);

    /**
     * 根据用户 博客分类 进行查询
     * @param user
     * @param catalog
     * @param pageIndex
     * @param pageSize
     * @return
     */
    Page<Blog> listBlogsByCatalogAndUser(User user, Catalog catalog, int pageIndex, int pageSize);

    /**
     * 根据用户 标签 进行查询
     * @param user
     * @param tag
     * @param pageable
     * @return
     */
    Page<Blog> listBlogsByUserAndTag(User user, String tag, Pageable pageable);

    /**
     * 阅读量递增
     *
     * @param id
     */
    void readingIncrease(Long id);

    /**
     * 点赞
     *
     * @param blogId
     */
    Vote createVote(Long blogId);

    /**
     * 取消点赞 (同时删除了"孤儿"vote)
     *
     * @param blogId
     * @param voteId
     */
    void removeVote(Long blogId, Long voteId);


    /**
     * 根据用户、分类  查询博客数量
     * @param catalog
     * @return
     */
    Long countBlogsByCatalog(Catalog catalog);

    /**
     * 查出所有 blog
     * @return
     */
    List<Blog> listAllBlogs();

    /**
     * 根据用户或关键词查询博客
     * @param user
     * @param title
     * @param pageable
     * @return
     */
    Page<Blog> listBlogsByUserAndTitle(User user, String title, Pageable pageable);
}
