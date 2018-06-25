package top.aceofspades.blog.vo;

import top.aceofspades.blog.domain.Catalog;

/**
 * catalog VO.
 * @author ace
 * @version 1.0
 * @since 2018/6/13 22:33
 */
public class CatalogVO {
    private String username;
    private Catalog catalog;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Catalog getCatalog() {
        return catalog;
    }

    public void setCatalog(Catalog catalog) {
        this.catalog = catalog;
    }
}
