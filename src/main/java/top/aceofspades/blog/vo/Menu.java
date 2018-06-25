package top.aceofspades.blog.vo;

/**
 * 菜单 值对象.
 * @author ace
 * @version 1.0
 * @since 2018/6/3 18:16
 */
public class Menu {

    private String name;//菜单名称

    private String url;//菜单 url

    public Menu(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
