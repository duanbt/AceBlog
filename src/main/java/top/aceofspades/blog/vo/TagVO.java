package top.aceofspades.blog.vo;

/**
 * Tag 值对象.
 * @author ace
 * @version 1.0
 * @since 2018/6/15 20:49
 */
public class TagVO {

    private String name;//标签名称
    private Long count;//标签出现次数

    public TagVO(String name, Long count) {
        this.name = name;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
