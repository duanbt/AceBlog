package top.aceofspades.blog.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.aceofspades.blog.domain.Catalog;
import top.aceofspades.blog.domain.User;
import top.aceofspades.blog.repository.CatalogRepository;
import top.aceofspades.blog.service.BlogService;
import top.aceofspades.blog.service.CatalogService;
import top.aceofspades.blog.util.ConstraintViolationExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Optional;

/**
 * Catalog 服务接口实现.
 *
 * @author ace
 * @version 1.0
 * @since 2018/6/13 21:50
 */
@Service
public class CatalogServiceImpl implements CatalogService {

    @Autowired
    private CatalogRepository catalogRepository;
    @Autowired
    private BlogService blogService;

    @Override
    public Catalog saveCatalog(Catalog catalog) {
        //查重后保存
        List<Catalog> list = catalogRepository.findByUserAndName(catalog.getUser(), catalog.getName());
        if (list != null && list.size() > 0) { //分类已存在
            throw new IllegalArgumentException("该分类已经存在了");
        }
        try {
            return catalogRepository.save(catalog);
        } catch (ConstraintViolationException e) {
            throw new IllegalArgumentException(ConstraintViolationExceptionHandler.getMessage(e));
        }
    }

    @Override
    public void removeCatalog(Long catalogId) {
        //查找该分类下是否有博客，有则抛出异常
        Optional<Catalog> optionalCatalog = this.getCatalogById(catalogId);
        if (optionalCatalog.isPresent()) {
            Long count = blogService.countBlogsByCatalog(optionalCatalog.get());
            if (count > 0) {
                throw new IllegalArgumentException("该分类下存在博客,不能删除");
            }
            catalogRepository.deleteById(catalogId);
        } else {
            throw new IllegalArgumentException("不存在该分类");
        }
    }

    @Override
    public Optional<Catalog> getCatalogById(Long id) {
        return catalogRepository.findById(id);
    }

    @Override
    public List<Catalog> listCatalogs(User user) {
        return catalogRepository.findByUser(user);
    }
}
