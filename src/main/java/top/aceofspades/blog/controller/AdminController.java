package top.aceofspades.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import top.aceofspades.blog.service.EsBlogService;
import top.aceofspades.blog.vo.Menu;
import top.aceofspades.blog.vo.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * 管理员控制器.
 *
 * @author ace
 * @version 1.0
 * @since 2018/6/3 11:48
 */
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')") //限定 admin角色可访问
public class AdminController {

    @Autowired
    private EsBlogService esBlogService;

    /**
     * 获取后台管理主页
     *
     * @param model
     * @return
     */
    @GetMapping
    public ModelAndView listUsers(Model model) {
        List<Menu> list = new ArrayList<>();
        list.add(new Menu("用户管理", "/users"));
        list.add(new Menu("博客管理", "/manageBlog"));
        model.addAttribute("list", list);
        return new ModelAndView("admin/index", "model", model);
    }

    /**
     * 导入ES索引库
     *
     * @return
     */
    @GetMapping("/importAll")
    public ResponseEntity<Response> importAllEsBlog() throws Exception {
        esBlogService.importAllEsBlog();
        return ResponseEntity.ok().body(new Response(true, "导入成功"));
    }

    /**
     * 清空ES索引库
     *
     * @return
     */
    @GetMapping("/removeAll")
    public ResponseEntity<Response> removeAll() {
        esBlogService.removeAllEsBlog();
        return ResponseEntity.ok().body(new Response(true, "清空索引库成功"));
    }

    /**
     * 获得索引库数量
     *
     * @return
     */
    @GetMapping("/countEs")
    public ResponseEntity<Response> countEsBlog() {
        Long count = null;
        count = esBlogService.countAllEsBlog();
        return ResponseEntity.ok().body(new Response(true, "索引库数量为" + count));
    }

}
