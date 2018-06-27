package top.aceofspades.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import top.aceofspades.blog.domain.Blog;
import top.aceofspades.blog.domain.User;
import top.aceofspades.blog.service.BlogService;
import top.aceofspades.blog.vo.Response;

import java.util.List;

/**
 * Blog 控制器. (管理用)
 *
 * @author ace
 * @version 1.0
 * @since 2018/6/18 11:35
 */
@Controller
@RequestMapping("/manageBlog")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class BlogController {

    @Autowired
    private BlogService blogService;

    @Qualifier("userServiceImpl")
    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * 查询所有博客
     *
     * @param async     默认false：非异步加载；
     * @param pageIndex 页码
     * @param pageSize  每页记录数
     * @param username  账号
     * @param title     标题
     * @param model
     * @return
     */
    @GetMapping
    public ModelAndView list(
            @RequestParam(required = false) boolean async,
            @RequestParam(required = false, defaultValue = "1") int pageIndex,
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            @RequestParam(required = false, defaultValue = "") String username,
            @RequestParam(required = false, defaultValue = "") String title,
            Model model) {
        User user = (User) userDetailsService.loadUserByUsername(username);
        Page<Blog> page = blogService.listBlogsByUserAndTitle(user, title, PageRequest.of(pageIndex - 1, pageSize));
        List<Blog> list = page.getContent();
        model.addAttribute("page", page);
        model.addAttribute("blogList", list);
        return new ModelAndView(
                async ? "blogs/list :: #mainContainerReplace" : "blogs/list",
                "manageBlogModel", model);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteBlog(@PathVariable("id") Long id) {
        blogService.removeBlog(id);
        return ResponseEntity.ok().body(new Response(true, "处理成功"));
    }
}
