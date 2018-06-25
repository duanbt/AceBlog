package top.aceofspades.blog.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import top.aceofspades.blog.domain.User;
import top.aceofspades.blog.domain.es.EsBlog;
import top.aceofspades.blog.vo.TagVO;

import java.util.List;

/**
 * EsBlog 服务接口.
 * @author ace
 * @version 1.0
 * @since 2018/6/15 20:33
 */
public interface EsBlogService {

    /**
     * 清空es中Esblog, 将数据库中的数据 重新导入 elasticsearch
     */
    void importAllEsBlog();

    /**
     * 清空索引库
     */
    void removeAllEsBlog();

    /**
     * 查询总数量
     * @return
     */
    Long countAllEsBlog();

    /**
     * 删除EsBlog
     * @param id
     */
    void removeEsBlog(String id);

    /**
     * 更新EsBlog
     * @param esBlog
     */
    EsBlog updateEsBlog(EsBlog esBlog);

    /**
     * 根据Blog 的 id 获取 EsBlog
     * @param blogId
     * @return
     */
    EsBlog getEsBlogByBlogId(Long blogId);

    /**
     * 最新博客列表（分页）
     * @param keyword
     * @param pageable
     * @return
     */
    Page<EsBlog> listNewestEsBlogs(String keyword, Pageable pageable);

    /**
     * 最热博客列表 （分页）
     * @param keyword
     * @param pageable
     * @return
     */
    Page<EsBlog> listHotestEsBlogs(String keyword, Pageable pageable);

    /**
     * 根据标签 查询博客列表（分页）
     * @param tag
     * @param pageable
     * @return
     */
    Page<EsBlog> listEsBlogsByTag(String tag, Pageable pageable);

    /**
     * 博客列表（分页）
     * @param pageable
     * @return
     */
    Page<EsBlog> listEsBlogs(Pageable pageable);

    /**
     * 最新 count 篇 博客
     * @param count
     * @return
     */
    List<EsBlog> listNewestEsBlogs(int count);

    /**
     * 最热 count 篇 博客
     * @param count
     * @return
     */
    List<EsBlog> listHotestEsBlogs(int count);

    /**
     * 最热 前 count 个 标签
     * @param  count
     * @return
     */
    List<TagVO> listTopTags(int count);

    /**
     * 最热 前 count 位 用户
     * @param count
     * @return
     */
    List<User> listTopUsers(int count);



}
