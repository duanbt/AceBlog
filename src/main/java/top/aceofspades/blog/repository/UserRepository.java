package top.aceofspades.blog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import top.aceofspades.blog.domain.Authority;
import top.aceofspades.blog.domain.User;

import java.util.Collection;
import java.util.List;

/**
 * 用户仓库.
 * @author ace
 * @version 1.0
 * @since 2018/6/3 17:45
 */
public interface UserRepository extends JpaRepository<User, Long>{

    /**
     * 根据用户账号模糊查询 用户列表
     * @param username
     * @param pageable
     * @return
     */
    Page<User> findByUsernameLike(String username, Pageable pageable);

    /**
     * 根据账号查询用户
     * @param username
     * @return
     */
    User findByUsername(String username);

    /**
     * 根据用户名集合查询用户列表
     * @param usernames
     * @return
     */
    List<User> findByUsernameIn(Collection<String> usernames);

    /**
     * 根据authority 获取 用户
     * @param authority
     * @return
     */
    List<User> findByAuthoritiesContains(Authority authority);
}
