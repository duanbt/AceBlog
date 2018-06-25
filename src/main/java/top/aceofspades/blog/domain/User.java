package top.aceofspades.blog.domain;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * User 实体.
 *
 * @author ace
 * @version 1.0
 * @since 2018/6/3 17:15
 */
@Entity //实体
public class User implements UserDetails {

    @Id//主键
    @GeneratedValue(strategy = GenerationType.IDENTITY)//自增长策略
    private Long id;

    @NotEmpty(message = "账号不能为空")
    @Size(min = 3, max = 20, message = "账号必须为3~20个字符")
    @Column(nullable = false, length = 20, unique = true)//映射为唯一字段，值不能为空
    private String username; //用户账号

    @NotEmpty(message = "密码不能为空")
    @Size(max = 100)
    @Column(length = 100)
    private String password;//登录密码

    @NotEmpty(message = "邮箱不能为空")
    @Size(max = 50)
    @Email(message = "邮箱格式不对")
    @Column(nullable = false, length = 50, unique = true)
    private String email;//邮箱

    @Column
    private String avatar;//头像图片地址

    @ManyToMany(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)//级联，急加载
    @JoinTable(name = "user_authority",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "authority_id", referencedColumnName = "id"))
    private List<Authority> authorities;

    protected User() { //JPA规范要求：无参构造函数，设为protected防止直接使用
    }

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password){this.password = password;}
    /**
     * 加密密码
     * @param password
     */
    public void encodePassword(String password) {
        if (StringUtils.isNoneEmpty(password)) {
            PasswordEncoder encoder = new BCryptPasswordEncoder();
            this.password = encoder.encode(password);
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
    }


    public void setAuthorities(List<Authority> authorities) {
        this.authorities = authorities;
    }

    /*---分割线，以下方法为 UserDetails 的---*/
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public List<Authority> getAuthorities() {
        //需将 List<Authority> 转成 List<SimpleGrantedAuthority>，否则前端拿不到角色列表名称
      /*  List<SimpleGrantedAuthority> simpleAuthorities = new ArrayList<>();
        for (GrantedAuthority authority : this.authorities) {
            simpleAuthorities.add(new SimpleGrantedAuthority(authority.getAuthority()));
        }
        return simpleAuthorities;*/
      return this.authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
