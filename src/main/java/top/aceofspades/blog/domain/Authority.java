package top.aceofspades.blog.domain;

import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

/**
 * 角色实体.
 * @author ace
 * @version 1.0
 * @since 2018/6/5 13:00
 */

@Entity
public class Authority implements GrantedAuthority{

    public static final String NAME_ADMIN = "ROLE_ADMIN";
    public static final String NAME_USER = "ROLE_USER";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;//角色的唯一标识

    @Column(nullable = false) //值不能为空
    private String name; //角色名称

    @Override
    public String getAuthority() {
        return this.name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    protected Authority(){}

    public Authority(String name) {
        this.name = name;
    }
}
