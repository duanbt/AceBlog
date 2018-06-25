package top.aceofspades.blog.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import top.aceofspades.blog.domain.Authority;
import top.aceofspades.blog.domain.User;
import top.aceofspades.blog.repository.UserRepository;
import top.aceofspades.blog.service.AuthorityService;
import top.aceofspades.blog.service.UserService;
import top.aceofspades.blog.util.ConstraintViolationExceptionHandler;
import top.aceofspades.blog.util.UpdateUtil;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * User 服务接口实现.
 *
 * @author ace
 * @version 1.0
 * @since 2018/6/3 18:06
 */
@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorityService authorityService;

    @Transactional
    @Override
    public User saveOrUpdateUser(User user) {

        if (null == user.getId()) {//新增用户情况
            user.encodePassword(user.getPassword());
        } else {//更新用户情况
            //判断密码是否发生了更改，如果更改了，设置加密密码
            if (null != user.getPassword()) {
                String rawPassword = getUserById(user.getId()).get().getPassword();
                PasswordEncoder encoder = new BCryptPasswordEncoder();
                String encodedPassword = encoder.encode(user.getPassword());
                boolean isMatch = encoder.matches(rawPassword, encodedPassword);
                if (!isMatch) {
                    user.encodePassword(user.getPassword());
                }
            }
            User originalUser = getUserById(user.getId()).get();
            UpdateUtil.coverNullProperties(originalUser, user);
        }
        try {
            return userRepository.save(user);
        } catch (ConstraintViolationException e) {
            throw new IllegalArgumentException(ConstraintViolationExceptionHandler.getMessage(e));
        }
    }

    @Transactional
    @Override
    public User registerUser(User user) {
        List<Authority> authorities = new ArrayList<>();
        authorities.add(authorityService.getAuthorityByName(Authority.NAME_USER).get());
        user.setAuthorities(authorities);
        return this.saveOrUpdateUser(user);
    }

    @Transactional
    @Override
    public void removeUser(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void removeUserUserInBatch(List<User> users) {
        userRepository.deleteInBatch(users);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> listUsers() {
        return userRepository.findAll();
    }

    @Override
    public Page<User> listUsersByUsernameLike(String username, Pageable pageable) {
        //模糊查询
        username = "%" + username + "%";
        return userRepository.findByUsernameLike(username, pageable);
    }

    @Override
    public List<User> listUsersByUsernames(Collection<String> usernames) {
        return userRepository.findByUsernameIn(usernames);
    }

    @Override
    public Long countUser() {
        return userRepository.count();
    }

    @Override
    public List<User> getUserByAuthority(Authority authority) {
        List<User> userList = userRepository.findByAuthoritiesContains(authority);
        return userList;
    }


    /**
     * 覆盖自 UserDetailsService(Spring security中的接口)
     *
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username);
    }
}
