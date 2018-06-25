package top.aceofspades.blog.service;

import top.aceofspades.blog.domain.Catalog;
import top.aceofspades.blog.domain.User;

import java.util.List;
import java.util.Optional;

/**
 * Catalog 服务接口.
 *
 * @author ace
 * @version 1.0
 * @since 2018/6/13 21:47
 */

public interface CatalogService {

    /**
     * 保存Catalog
     * @param catalog
     * @return
     */
    Catalog saveCatalog(Catalog catalog);

    /**
     * 删除Catalog
     * @param catalogId catalog id
     */
    void removeCatalog(Long catalogId);

    /**
     * 根据id获取Catalog
     * @param id
     * @return
     */
    Optional<Catalog> getCatalogById(Long id);

    /**
     * 获取Catalog列表
     * @param user
     * @return
     */
    List<Catalog> listCatalogs(User user);
}
