package top.aceofspades.blog.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import top.aceofspades.blog.domain.Authority;
import top.aceofspades.blog.domain.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * User 服务接口.
 * @author ace
 * @version 1.0
 * @since 2018/6/3 17:51
 */
public interface UserService {

    /**
     * 保存或更新用户
     * @param user
     * @return
     */
    User saveOrUpdateUser (User user);

    /**
     * 注册用户
     * @param user
     * @return
     */
    User registerUser(User user);

    /**
     * 删除用户
     * @param id
     */
    void removeUser(Long id);

    /**
     * 批量删除用户
     * @param users
     */
    void removeUserUserInBatch(List<User> users);

    /**
     * 根据id获取用户
     * @param id
     * @return
     */
    Optional<User> getUserById(Long id);

    /**
     * 获取用户列表
     * @return
     */
    List<User> listUsers();

    /**
     * 根据用户账号进行分页模糊查询
     * @param username
     * @param pageable
     * @return
     */
    Page<User> listUsersByUsernameLike(String username, Pageable pageable);


    /**
     * 根据用户名集合,查询用户列表
     * @param usernames
     * @return
     */
    List<User> listUsersByUsernames(Collection<String> usernames);


    /**
     * 获取当前注册用户数
     * @return
     */
    Long countUser();

    /**
     * 通过authority 获取 user
     * @param authority
     * @return
     */
    List<User> getUserByAuthority(Authority authority);
}
