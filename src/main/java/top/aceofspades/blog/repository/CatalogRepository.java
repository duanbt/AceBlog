package top.aceofspades.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import top.aceofspades.blog.domain.Catalog;
import top.aceofspades.blog.domain.User;

import java.util.List;

/**
 * Catalog Repository.
 * @author ace
 * @version 1.0
 * @since 2018/6/13 21:36
 */
public interface CatalogRepository extends JpaRepository<Catalog, Long> {

    /**
     * 根据用户查询分类
     * @param user
     * @return
     */
    List<Catalog> findByUser(User user);

    /**
     * 根据用户、分类名称查询
     * @param user
     * @param name
     * @return
     */
    List<Catalog> findByUserAndName(User user, String name);
}
