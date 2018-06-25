package top.aceofspades.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import top.aceofspades.blog.domain.Blog;
import top.aceofspades.blog.domain.Catalog;
import top.aceofspades.blog.domain.User;
import top.aceofspades.blog.domain.Vote;
import top.aceofspades.blog.service.BlogService;
import top.aceofspades.blog.service.CatalogService;
import top.aceofspades.blog.service.UserService;
import top.aceofspades.blog.util.ConstraintViolationExceptionHandler;
import top.aceofspades.blog.util.httpclient.http.HttpAPIService;
import top.aceofspades.blog.vo.Response;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Optional;

/**
 * 用户个人主页控制器.
 *
 * @author ace
 * @version 1.0
 * @since 2018/6/3 14:35
 */
@Controller
@RequestMapping("/u")
public class UserSpaceController {

    @Autowired
    private UserService userService;
    @Qualifier("userServiceImpl")
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private BlogService blogService;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private HttpAPIService httpAPIService;

    @Value("${file.server.url}")
    private String fileServerUrl;
    @Value("${file.server.native.url}")
    private String fileServerNativeUrl;

    /**
     * 用户主页
     *
     * @param username
     * @return
     */
    @GetMapping("/{username}")
    public String userSpace(@PathVariable("username") String username, Model model) {
        User user = (User) userDetailsService.loadUserByUsername(username);
        model.addAttribute("user", user);
        return "redirect:/u/" + username + "/blogs";
    }


    /**
     * 获取用户的博客列表
     *
     * @param username  用户账号
     * @param order     排序方式
     * @param catalogId 分类
     * @param keyword   搜索关键字
     * @param async     是否返回部分页面
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @GetMapping("/{username}/blogs")
    public String listBlogsByOrder(
            @PathVariable("username") String username,
            @RequestParam(value = "order", required = false, defaultValue = Blog.BY_TIME) String order,
            @RequestParam(value = "catalog", required = false) Long catalogId,
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(value = "async", required = false) boolean async,
            @RequestParam(value = "pageIndex", required = false, defaultValue = "1") int pageIndex,
            @RequestParam(value = "pageSize", required = false, defaultValue = "6") int pageSize, Model model) {

        User user = (User) userDetailsService.loadUserByUsername(username);
        Page<Blog> page = null;

        if (catalogId != null && catalogId > 0) {//分类查询
            Optional<Catalog> optionalCatalog = catalogService.getCatalogById(catalogId);
            if (optionalCatalog.isPresent()) {
                page = blogService.listBlogsByCatalogAndUser(user, optionalCatalog.get(), pageIndex, pageSize);
            }
            order = "";
        } else if (order.equals(Blog.BY_HOT)) {//最热查询
            page = blogService.listBlogsByUserAndKeywordByHot(user, keyword, PageRequest.of(pageIndex - 1, pageSize));
        } else if (order.equals(Blog.BY_TIME)) {//最新查询
            page = blogService.listBlogsByUserAndKeywordByTime(user, keyword, PageRequest.of(pageIndex - 1, pageSize));
        }


        model.addAttribute("user", user);
        model.addAttribute("order", order);
        model.addAttribute("catalogId", catalogId);
        model.addAttribute("keyword", keyword);
        model.addAttribute("page", page);
        model.addAttribute("blogList", page.getContent());
        return (async ? "userspace/u :: #mainContainerReplace" : "userspace/u");
    }

    /**
     * 获取博客展示页面
     *
     * @param username 博客所属用户账号
     * @param id       blog的id
     * @return
     */
    @GetMapping("/{username}/blogs/{id}")
    public String getBlogById(@PathVariable("username") String username, @PathVariable("id") Long id, Model model) {
        Optional<Blog> optionalBlog = blogService.getBlogById(id);
        Blog blog = null;
        if (optionalBlog.isPresent()) {
            blog = optionalBlog.get();
        }
        User principal = null; //当前操作用户

        //判断操作用户是否是博客所有者
        boolean isBlogOwner = false;
        if (SecurityContextHolder.getContext().getAuthentication() != null
                && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                && !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
            principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal != null && username.equals(principal.getUsername())) {//principal为当前操作用户
                isBlogOwner = true;
            }
        }
        if (!isBlogOwner) {//不是博客所有者，阅读量+1
            //每次读取，简单的认为阅读量增加1次（策略待完善）
            blogService.readingIncrease(id);
        }

        //判断操作用户的点赞情况,点过赞则返回vote对象，否则返回null
        Vote currentVote = null;
        if (principal != null) {
            for (Vote vote : blog.getVotes()) {
                if (vote.getUser().getUsername().equals(principal.getUsername())) {
                    currentVote = vote;
                    break;
                }
            }
        }
        model.addAttribute("currentVote", currentVote);
        model.addAttribute("isBlogOwner", isBlogOwner);
        model.addAttribute("blog", blog);
        return "userspace/blog";
    }

    /**
     * 获取新增博客的页面
     *
     * @param username
     * @param model
     * @return
     */
    @GetMapping("/{username}/blogs/edit")
    @PreAuthorize("authentication.name.equals(#username)")
    public ModelAndView createBlog(@PathVariable("username") String username, Model model) {
        User user = (User) userDetailsService.loadUserByUsername(username);
        List<Catalog> catalogs = catalogService.listCatalogs(user);

        model.addAttribute("blog", new Blog(null, null, null));
        model.addAttribute("fileServerUrl", fileServerUrl);//文件服务器地址
        model.addAttribute("catalogs", catalogs);
        return new ModelAndView("userspace/blogedit", "blogModel", model);
    }

    /**
     * 获得编辑博客的界面
     *
     * @param username
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/{username}/blogs/edit/{id}")
    @PreAuthorize("authentication.name.equals(#username)")
    public ModelAndView editBlog(@PathVariable("username") String username, @PathVariable("id") Long id, Model model) {
        User user = (User) userDetailsService.loadUserByUsername(username);
        List<Catalog> catalogs = catalogService.listCatalogs(user);

        model.addAttribute("blog", blogService.getBlogById(id).get());
        model.addAttribute("fileServerUrl", fileServerUrl);
        model.addAttribute("catalogs", catalogs);
        return new ModelAndView("userspace/blogedit", "blogModel", model);
    }

    /**
     * 保存或更新博客
     *
     * @param username
     * @param blog
     * @return
     */
    @PostMapping("/{username}/blogs/edit")
    @PreAuthorize("authentication.name.equals(#username)")
    public ResponseEntity<Response> saveBlog(@PathVariable("username") String username, @RequestBody Blog blog) {
        //如果没选择分类,则返回错误信息
        if (blog.getCatalog().getId() == null) {
            return ResponseEntity.ok().body(new Response(false, "请选择分类"));
        }
        try {
            blogService.saveBlog(blog);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        String redirectUrl = "/u/" + username + "/blogs/" + blog.getId();
        return ResponseEntity.ok().body(new Response(true, "处理成功", redirectUrl));
    }

    /**
     * 删除博客
     *
     * @param username
     * @param id       blog id
     * @return
     */
    @DeleteMapping("/{username}/blogs/{id}")
    @PreAuthorize("authentication.name.equals(#username)")
    public ResponseEntity<Response> deleteBlog(@PathVariable("username") String username, @PathVariable("id") Long id) {
        try {
            blogService.removeBlog(id);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }

        String redirectUrl = "/u/" + username + "/blogs";
        return ResponseEntity.ok().body(new Response(true, "处理成功", redirectUrl));
    }

    /**
     * 获取个人设置页面
     *
     * @param username
     * @param model
     * @return
     */
    @GetMapping("/{username}/profile")
    @PreAuthorize("authentication.name.equals(#username)")
    public ModelAndView profile(@PathVariable("username") String username, Model model) {
        User user = (User) userDetailsService.loadUserByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("fileServerUrl", fileServerUrl);
        return new ModelAndView("userspace/profile", "userModel", model);
    }

    /**
     * 保存个人设置
     *
     * @param username
     * @param user
     * @return
     */
    @PostMapping("/{username}/profile")
    @PreAuthorize("authentication.name.equals(#username)")
    public ResponseEntity<Response> saveProfile(@PathVariable("username") String username, User user) {
        try {
            userService.saveOrUpdateUser(user);
        } catch (TransactionSystemException e) {
            Throwable t = e.getCause();
            while ((t != null) && !(t instanceof ConstraintViolationException)) {
                t = t.getCause();
            }
            return ResponseEntity.ok().body(
                    new Response(false, ConstraintViolationExceptionHandler.getMessage((ConstraintViolationException) t)));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.ok().body(
                    new Response(false, "账号或邮箱重复"));
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true, "处理成功", user));
    }

    /**
     * 获取编辑头像的页面
     *
     * @param username
     * @param model
     * @return
     */
    @GetMapping("/{username}/avatar")
    @PreAuthorize("authentication.name.equals(#username)")
    public ModelAndView avatar(@PathVariable("username") String username, Model model) {
        User user = (User) userDetailsService.loadUserByUsername(username);
        model.addAttribute("user", user);
        return new ModelAndView("userspace/avatar", "userModel", model);
    }

    /**
     * 保存头像
     *
     * @param username
     * @param user
     * @return
     */
    @PostMapping("/{username}/avatar")
    @PreAuthorize("authentication.name.equals(#username)")
    public ResponseEntity<Response> saveAvatar(@PathVariable("username") String username, @RequestBody User user) {
        try {
            User oldUser = (User) userDetailsService.loadUserByUsername(username);
            String avatarUrl = oldUser.getAvatar();
            if (avatarUrl != null) {
                //发送http请求 删除原头像
                String fileId = avatarUrl.substring(avatarUrl.lastIndexOf("/"));
                httpAPIService.doDelete(fileServerNativeUrl + fileId);
            }
            userService.saveOrUpdateUser(user);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true, "处理成功", user.getAvatar()));
    }

}
