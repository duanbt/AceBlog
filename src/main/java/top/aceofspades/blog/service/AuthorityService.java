package top.aceofspades.blog.service;

import top.aceofspades.blog.domain.Authority;

import java.util.Optional;

/**
 * Authority 服务接口.
 * @author ace
 * @version 1.0
 * @since 2018/6/5 13:20
 */
public interface AuthorityService {

    /**
     * 根据Id查询 Authority
     * @param id
     * @return
     */
    Optional<Authority> getAuthorityById(Long id);

    /**
     * 新增权限
     * @param authority
     * @return
     */
    Authority saveAuthority(Authority authority);

    /**
     * 根据角色名称 查询 Authority
     * @param name
     * @return
     */
    Optional<Authority> getAuthorityByName(String name);

    /**
     * 查询Authority数量
     * @return
     */
    long count();
}
