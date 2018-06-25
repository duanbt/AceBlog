package top.aceofspades.blog.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.aceofspades.blog.domain.Authority;
import top.aceofspades.blog.repository.AuthorityRepository;
import top.aceofspades.blog.service.AuthorityService;

import java.util.Optional;

/**
 * Authority 服务接口实现.
 *
 * @author ace
 * @version 1.0
 * @since 2018/6/5 13:23
 */
@Service
public class AuthorityServiceImpl implements AuthorityService {
    @Autowired
    private AuthorityRepository authorityRepository;

    @Override
    public Optional<Authority> getAuthorityById(Long id) {
        return authorityRepository.findById(id);
    }

    @Override
    public Authority saveAuthority(Authority authority) {
        return authorityRepository.save(authority);
    }

    @Override
    public Optional<Authority> getAuthorityByName(String name) {
        return authorityRepository.findByName(name);
    }

    @Override
    public long count() {
        return authorityRepository.count();
    }

}
