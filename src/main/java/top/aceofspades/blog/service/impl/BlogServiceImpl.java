package top.aceofspades.blog.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import top.aceofspades.blog.domain.Blog;
import top.aceofspades.blog.domain.Catalog;
import top.aceofspades.blog.domain.User;
import top.aceofspades.blog.domain.Vote;
import top.aceofspades.blog.listener.event.BlogRemoveEvent;
import top.aceofspades.blog.listener.event.BlogSaveEvent;
import top.aceofspades.blog.repository.BlogRepository;
import top.aceofspades.blog.service.BlogService;
import top.aceofspades.blog.service.VoteService;
import top.aceofspades.blog.util.ConstraintViolationExceptionHandler;
import top.aceofspades.blog.util.UpdateUtil;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Optional;

/**
 * Blog 服务接口实现.
 *
 * @author ace
 * @version 1.0
 * @since 2018/6/6 20:57
 */
@Service
public class BlogServiceImpl implements BlogService {

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private VoteService voteService;

    @Autowired
    private ApplicationContext ctx;

    @Transactional
    @Override
    public Blog saveBlog(Blog blog) {
        boolean isNew = (blog.getId() == null);
        if (isNew) {//新增
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            blog.setUser(user);
        } else {//更新
            Optional<Blog> optionalBlog = getBlogById(blog.getId());
            if (optionalBlog.isPresent()) {
                Blog originalBlog = optionalBlog.get();
                UpdateUtil.coverNullProperties(originalBlog, blog); //用数据库中查出来的数据填充blog中的null或为0的数据
            }
        }
        Blog returnBlog = null;
        try {
            returnBlog = blogRepository.save(blog);
        } catch (ConstraintViolationException e) {
            throw new IllegalArgumentException(ConstraintViolationExceptionHandler.getMessage(e));
        }
        ctx.publishEvent(new BlogSaveEvent(this, returnBlog, isNew)); //发布事件
        return returnBlog;
    }

    @Transactional
    @Override
    public void removeBlog(Long id) {
        blogRepository.deleteById(id);
        ctx.publishEvent(new BlogRemoveEvent(this, id));//发布事件
    }

    @Override
    public Optional<Blog> getBlogById(Long id) {
        return blogRepository.findById(id);
    }

    @Override
    public Page<Blog> listBlogsByUserAndKeywordByTime(User user, String keyword, Pageable pageable) {
        //模糊查询
        String title = "%" + keyword + "%";
        String tag = title;
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        Page<Blog> blogs = blogRepository.findByUserAndTitleLikeOrUserAndTagsLike(user, title, user, tag, pageable);
        return blogs;
    }

    @Override
    public Page<Blog> listBlogsByUserAndKeywordByHot(User user, String keyword, Pageable pageable) {
        String title = "%" + keyword + "%";
        String tag = title;
        Sort sort = new Sort(Sort.Direction.DESC, "readSize", "voteSize", "createTime");
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        Page<Blog> blogs = blogRepository.findByUserAndTitleLikeOrUserAndTagsLike(user, title, user, tag, pageable);
        return blogs;
    }

    @Override
    public Page<Blog> listBlogsByCatalogAndUser(User user, Catalog catalog, int pageIndex, int pageSize) {
        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize);
        Page<Blog> blogs = blogRepository.findByUserAndCatalog(user, catalog, pageable);
        return blogs;
    }

    @Override
    public Page<Blog> listBlogsByUserAndTag(User user, String tag, Pageable pageable) {
        tag = "%" + tag + "%";
        Page<Blog> blogs = blogRepository.findByUserAndTagsLike(user, tag, pageable);
        return blogs;
    }

    @Override
    public void readingIncrease(Long id) {
        Optional<Blog> optionalBlog = blogRepository.findById(id);
        if (optionalBlog.isPresent()) {
            Blog blog = optionalBlog.get();
            blog.setReadSize(blog.getReadSize() + 1);//阅读量 + 1
            this.saveBlog(blog);
        }
    }


    @Override
    public Vote createVote(Long blogId) {
        Optional<Blog> optionalBlog = blogRepository.findById(blogId);
        if (optionalBlog.isPresent()) {
            Blog blog = optionalBlog.get();
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Vote vote = new Vote(user);
            boolean isExist = blog.addVote(vote);
            if (isExist) {
                throw new IllegalArgumentException("已点过赞");
            }
            Blog resultBlog = this.saveBlog(blog);

            Vote resultVote = null;
            for (Vote v : resultBlog.getVotes()) {
                if (v.getUser().getId().equals(user.getId())) {
                    resultVote = v;
                    break;
                }
            }
            return resultVote;
        }
        throw new IllegalArgumentException("该博客不存在");
    }

    @Override
    public void removeVote(Long blogId, Long voteId) {
        Optional<Blog> optionalBlog = blogRepository.findById(blogId);
        if (optionalBlog.isPresent()) {
            Blog blog = optionalBlog.get();
            blog.removeVote(voteId);
            this.saveBlog(blog);
        }
        voteService.removeVote(voteId);
    }

    @Override
    public Long countBlogsByCatalog(Catalog catalog) {
        Blog blog = new Blog(catalog);
        ExampleMatcher matcher = ExampleMatcher.matching() //构建匹配器
                .withIgnorePaths("readSize", "voteSize");  //忽略属性：是否关注。因为是基本类型，需要忽略掉

        Example<Blog> example = Example.of(blog, matcher);
        long count = blogRepository.count(example);
        return count;
    }

    @Override
    public List<Blog> listAllBlogs() {
        return blogRepository.findAll();
    }

    @Override
    public Page<Blog> listBlogsByUserAndTitle(User user, String title, Pageable pageable) {
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        ExampleMatcher matcher = ExampleMatcher.matching() //构建匹配器
                .withIgnoreNullValues() //忽略null值
        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) //字符串匹配方式为包含
                .withIgnorePaths("readSize", "voteSize");  //忽略属性：是否关注。因为是基本类型，需要忽略掉

        Blog blog = new Blog(null);
        blog.setUser(user);
        blog.setTitle(title);

        Example<Blog> example = Example.of(blog, matcher);
        Page<Blog> page = blogRepository.findAll(example, pageable);
        return page;
    }

}
