package top.aceofspades.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import top.aceofspades.blog.domain.Blog;
import top.aceofspades.blog.domain.Catalog;
import top.aceofspades.blog.domain.User;
import top.aceofspades.blog.service.CatalogService;
import top.aceofspades.blog.vo.CatalogVO;
import top.aceofspades.blog.vo.Response;

import java.util.List;
import java.util.Optional;

/**
 * 分类 控制器.
 *
 * @author ace
 * @version 1.0
 * @since 2018/6/13 22:03
 */
@Controller
@RequestMapping("/catalogs")
public class CatalogController {

    @Autowired
    private CatalogService catalogService;

    @Qualifier("userServiceImpl")
    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * 获取 个人主页分类列表
     *
     * @param username
     * @return
     */
    @GetMapping
    public String listCatalogs(@RequestParam("username") String username, Model model) {
        User user = (User) userDetailsService.loadUserByUsername(username);
        List<Catalog> catalogs = catalogService.listCatalogs(user);

        //判断操作用户是否是分类所有者
        boolean isOwner = false;
        if (SecurityContextHolder.getContext().getAuthentication() != null
                && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                && !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString()
                .equals("anonymousUser")) {
            User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal != null && user.getUsername().equals(principal.getUsername())) {
                isOwner = true;
            }
        }
        model.addAttribute("isCatalogOwner", isOwner);
        model.addAttribute("catalogs", catalogs);
        return "userspace/u :: #catalogReplace";
    }

    /**
     * 刷新博客编辑页面 分类下拉选择框
     *
     * @param username
     * @return
     */
    @GetMapping("/select")
    @PreAuthorize("authentication.name.equals(#username)")
    public String listCatalogsSelect(@RequestParam("username") String username, Model model) {

        User user = (User) userDetailsService.loadUserByUsername(username);
        List<Catalog> catalogs = catalogService.listCatalogs(user);
        model.addAttribute("blog", new Blog(null, null, null));
        model.addAttribute("catalogs", catalogs);
        return "userspace/blogedit :: #catalogSelectReplace";
    }


    /**
     * 新增或更新分类
     *
     * @param catalogVO {"username" : username,
     *                  "catalog" : {"id" : id,"name" :name}
     *                  }
     * @return
     */
    @PostMapping
    @PreAuthorize("authentication.name.equals(#catalogVO.username)")//验证本人操作
    public ResponseEntity<Response> create(@RequestBody CatalogVO catalogVO) {
        String username = catalogVO.getUsername();
        Catalog catalog = catalogVO.getCatalog();

        User user = (User) userDetailsService.loadUserByUsername(username);
        catalog.setUser(user);

        catalogService.saveCatalog(catalog);
        return ResponseEntity.ok().body(new Response(true, "处理成功"));
    }

    /**
     * 删除分类
     *
     * @param username
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("authentication.name.equals(#username)") //验证本人操作
    public ResponseEntity<Response> delete(@RequestParam("username") String username, @PathVariable("id") Long id) {
        catalogService.removeCatalog(id);
        return ResponseEntity.ok().body(new Response(true, "处理成功"));
    }

    /**
     * 获取分类新增页面
     *
     * @param model
     * @return
     */
    @GetMapping("/edit")
    public String getCatalogEdit(Model model) {
        Catalog catalog = new Catalog(null, null);
        model.addAttribute("catalog", catalog);
        return "userspace/catalogedit";
    }

    /**
     * 获取分类编辑页面
     *
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/edit/{id}")
    public String getCatalogById(@PathVariable("id") Long id, Model model) {
        Optional<Catalog> optionalCatalog = catalogService.getCatalogById(id);
        Catalog catalog = null;
        if (optionalCatalog.isPresent()) {
            catalog = optionalCatalog.get();
        }

        model.addAttribute("catalog", catalog);
        return "userspace/catalogedit";
    }

}
