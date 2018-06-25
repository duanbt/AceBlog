package top.aceofspades.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
 * 主页控制器.
 *
 * @author ace
 * @version 1.0
 * @since 2018/5/31 15:53
 */
@Controller
public class MainController {
    private static final Long ROLE_USER_AUTHORITY_ID = 2L;//博主角色Id
    @Autowired
    private UserService userService;
    @Value("${blog.user.max}")
    private Long maxUser;

    @GetMapping("/")
    public String root() {
        return "redirect:/index";
    }

    @GetMapping("/index")
    public String index() {
        return "redirect:/blogs";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/login-error")
    public String loginError(Model model) {
        model.addAttribute("loginError", true);
        model.addAttribute("errorMsg", "登录失败，用户名或密码错误");
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public ResponseEntity<Response> registerUser(User user) {

        //限制最多用户数
        if(userService.countUser() >= maxUser){
            return ResponseEntity.ok().body(new Response(false, "用户注册数已达上限，请邮件联系管理员，邮箱可在页面底部找到~"));
        }

        try {
            user = userService.registerUser(user);
        } catch (TransactionSystemException e) {
            Throwable t = e.getCause();
            while ((t != null) && !(t instanceof ConstraintViolationException)) {
                t = t.getCause();
            }
            return ResponseEntity.ok().body(
                    new Response(false, ConstraintViolationExceptionHandler.getMessage((ConstraintViolationException) t)));
        } catch (DataIntegrityViolationException e){
            return ResponseEntity.ok().body(
                    new Response(false, "账号或邮箱重复"));
        }catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true, "注册成功", user));
    }


    @GetMapping("/error")
    public String error() {
        return "error";
    }
}
