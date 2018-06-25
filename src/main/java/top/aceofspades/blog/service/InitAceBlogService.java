package top.aceofspades.blog.service;

/**
 * 初始化博客 服务接口.
 * @author ace
 * @version 1.0
 * @since 2018/6/20 12:28
 */
public interface InitAceBlogService {

    /**
     * 初始化系统权限
     */
    void initAuthority();

    /**
     * 初始化第一个管理员
     */
    void initAdmin();


}
