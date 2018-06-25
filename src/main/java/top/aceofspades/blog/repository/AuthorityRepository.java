package top.aceofspades.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import top.aceofspades.blog.domain.Authority;

import java.util.Optional;

/**
 * Authority 仓库.
 * @author ace
 * @version 1.0
 * @since 2018/6/5 13:09
 */
public interface AuthorityRepository extends JpaRepository<Authority, Long>{

    /**
     * 根据角色名称 获取 Authority
     * @param name Authority.NAME_ADMIN 或者 Authority.NAME_USER
     * @return
     */
    Optional<Authority> findByName(String name);
}
