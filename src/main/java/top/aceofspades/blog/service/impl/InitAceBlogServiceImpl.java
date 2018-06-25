package top.aceofspades.blog.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.aceofspades.blog.domain.Authority;
import top.aceofspades.blog.domain.User;
import top.aceofspades.blog.service.AuthorityService;
import top.aceofspades.blog.service.InitAceBlogService;
import top.aceofspades.blog.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 初始化博客 服务.
 * @author ace
 * @version 1.0
 * @since 2018/6/20 12:31
 */
@Service
public class InitAceBlogServiceImpl implements InitAceBlogService {

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private UserService userService;

    @Override
    public void initAuthority() {
        //如果不存在authority，则添加admin和user authority
        long count = authorityService.count();
        if(count <= 0){
            authorityService.saveAuthority(new Authority(Authority.NAME_ADMIN));
            authorityService.saveAuthority(new Authority(Authority.NAME_USER));
        }
    }

    @Override
    public void initAdmin() {
        //如果不存在 管理员，则添加一个管理员
        Optional<Authority> adminAuthority = authorityService.getAuthorityByName(Authority.NAME_ADMIN);
        List<User> userList = userService.getUserByAuthority(adminAuthority.get());
        if(userList.isEmpty()){
            User admin = new User("admin", "123456", "duanbt@foxmail.com");
            List<Authority> authorities = new ArrayList<>();
            authorities.add(adminAuthority.get());
            admin.setAuthorities(authorities);
            userService.saveOrUpdateUser(admin);
        }
    }
}
