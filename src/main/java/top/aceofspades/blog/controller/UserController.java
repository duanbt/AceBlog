package top.aceofspades.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import top.aceofspades.blog.domain.Authority;
import top.aceofspades.blog.domain.User;
import top.aceofspades.blog.service.AuthorityService;
import top.aceofspades.blog.service.UserService;
import top.aceofspades.blog.util.ConstraintViolationExceptionHandler;
import top.aceofspades.blog.vo.Response;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

/**
 * User 控制器.
 *
 * @author ace
 * @version 1.0
 * @since 2018/6/3 13:39
 */
@Controller
@RequestMapping("/users")
@PreAuthorize("hasAuthority('ROLE_ADMIN')") //限定 admin角色可访问
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private AuthorityService authorityService;


    /**
     * 查询所有用户
     *
     * @param async     默认false：非异步加载；
     * @param pageIndex 页码
     * @param pageSize  每页记录数
     * @param username  账号
     * @param model
     * @return
     */
    @GetMapping
    public ModelAndView list(
            @RequestParam(required = false) boolean async,
            @RequestParam(required = false, defaultValue = "1") int pageIndex,
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            @RequestParam(required = false, defaultValue = "") String username,
            Model model) {

        Page<User> page = userService.listUsersByUsernameLike(username, PageRequest.of(pageIndex - 1, pageSize));
        List<User> list = page.getContent();
        model.addAttribute("page", page);
        model.addAttribute("userList", list);
        return new ModelAndView(
                async ? "users/list :: #mainContainerReplace" : "users/list",
                "userModel", model);
    }

    /**
     * 获取新增用户表单页面
     *
     * @param model
     * @return
     */
    @GetMapping("/add")
    public ModelAndView createForm(Model model) {
        model.addAttribute("user", new User(null, null, null));
        return new ModelAndView("users/add", "userModel", model);
    }

    /**
     * 获取修改用户的界面
     *
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/edit/{id}")
    public ModelAndView modifyForm(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id).get();
        model.addAttribute("user", user);
        return new ModelAndView("users/edit", "userModel", model);
    }

    /**
     * 保存或修改用户
     *
     * @param user
     * @return
     */
    @PostMapping
    public ResponseEntity<Response> saveOrUpdateUser(User user, Long authorityId) throws Exception {
        List<Authority> authorities = new ArrayList<>();
        authorities.add(authorityService.getAuthorityById(authorityId).get());
        user.setAuthorities(authorities);

        userService.saveOrUpdateUser(user);
        return ResponseEntity.ok().body(new Response(true, "处理成功", user));
    }

    /**
     * 删除用户
     *
     * @param id
     * @param model
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Response> delete(@PathVariable Long id, Model model) {
        userService.removeUser(id);
        return ResponseEntity.ok().body(new Response(true, "处理成功"));
    }


}
