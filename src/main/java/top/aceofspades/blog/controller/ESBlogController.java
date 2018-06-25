package top.aceofspades.blog.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.aceofspades.blog.domain.Blog;
import top.aceofspades.blog.domain.User;
import top.aceofspades.blog.domain.es.EsBlog;
import top.aceofspades.blog.service.EsBlogService;
import top.aceofspades.blog.vo.TagVO;

import java.util.List;

/**
 * 主页Blog 控制器.
 *
 * @author ace
 * @version 1.0
 * @update 2018/6/16 13:40
 * @since 2018/6/3 14:30
 */
@Controller
@RequestMapping("/blogs")
public class ESBlogController {
    @Autowired
    private EsBlogService esBlogService;

    /**
     * 从ES 中 查询
     *
     * @param order
     * @param keyword
     * @param async
     * @param pageIndex
     * @param pageSize
     * @param model
     * @return
     */
    @GetMapping
    public String listEsBlogs(
            @RequestParam(value = "order", required = false, defaultValue = Blog.BY_TIME) String order,
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(value = "tag", required = false, defaultValue = "") String tag,
            @RequestParam(value = "async", required = false) boolean async,
            @RequestParam(value = "pageIndex", required = false, defaultValue = "1") int pageIndex,
            @RequestParam(value = "pageSize", required = false, defaultValue = "6") int pageSize,
            Model model) {

        Page<EsBlog> page = null;

        if (StringUtils.isNotEmpty(tag)) { //标签查询
            page = esBlogService.listEsBlogsByTag(tag, PageRequest.of(pageIndex-1, pageSize));
        } else if (order.equals(Blog.BY_TIME)) {//最新查询
            page = esBlogService.listNewestEsBlogs(keyword, PageRequest.of(pageIndex - 1, pageSize));
        } else if (order.equals(Blog.BY_HOT)) {//最热查询
            page = esBlogService.listHotestEsBlogs(keyword, PageRequest.of(pageIndex - 1, pageSize));
        }

        model.addAttribute("order", order);
        model.addAttribute("keyword", keyword);
        model.addAttribute("page", page);
        model.addAttribute("blogList", page.getContent());

        //首次访问页面加载
        if (!async) {
            List<EsBlog> newest = esBlogService.listNewestEsBlogs(5); //最新5篇博客
            model.addAttribute("newest", newest);

            List<EsBlog> hotest = esBlogService.listHotestEsBlogs(5);//最热5篇博客
            model.addAttribute("hotest", hotest);

            List<TagVO> tags = esBlogService.listTopTags(20);//最热20个标签
            model.addAttribute("tags", tags);

            List<User> users = esBlogService.listTopUsers(5);//最热5个用户(发布博客多的)
            model.addAttribute("users", users);

        }
        return (async ? "index :: #mainContainerReplace" : "index");
    }
}
