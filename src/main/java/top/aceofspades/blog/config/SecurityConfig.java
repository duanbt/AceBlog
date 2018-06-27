package top.aceofspades.blog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 安全配置类.
 *
 * @author ace
 * @version 1.0
 * @since 2018/6/5 13:15
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) //启用 expression-based 方法安全配置
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String KEY = "aceofspades.top";

    @Qualifier("userServiceImpl")
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);//设置密码加密的方式
        return authenticationProvider;
    }

    /**
     * 自定义配置
     *
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/css/**", "/js/**", "/fonts/**", "/images/**", "/index").permitAll()//设置静态资源及主页都可以访问
                .antMatchers("/h2-console/**").permitAll() // H2 web控制台 都可以访问
                .antMatchers("/admin/**").hasRole("ADMIN")//需要相应的角色才能访问
                .and().formLogin() //基于Form表单登录验证
                .loginPage("/login").failureUrl("/login-error")//自定义登陆页面和登陆失败页面
                .and().rememberMe().key(KEY)//启用remember me
                .and().exceptionHandling().accessDeniedPage("/error");//处理异常，拒绝访问就重定向到/error
        http.csrf().ignoringAntMatchers(("/h2-console/**"));//禁用 H2 控制台 CSRF防护
        http.headers().frameOptions().sameOrigin(); //允许:  网站页面在同源页面的 frame 中展示 (点击劫持相关)
        http.sessionManagement().maximumSessions(1).expiredUrl("/timeout");//设置同时在线人数，必须重写User的equal方法和hashCode方法才能起作用
    }

    /**
     * 认证信息管理
     * @param auth
     * @throws Exception
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception{
        auth.userDetailsService(userDetailsService);
        auth.authenticationProvider(authenticationProvider());
    }
}
