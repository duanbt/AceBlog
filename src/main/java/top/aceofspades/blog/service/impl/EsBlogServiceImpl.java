package top.aceofspades.blog.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ResultsExtractor;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;
import top.aceofspades.blog.domain.Blog;
import top.aceofspades.blog.domain.User;
import top.aceofspades.blog.domain.es.EsBlog;
import top.aceofspades.blog.repository.es.EsBlogRepository;
import top.aceofspades.blog.service.BlogService;
import top.aceofspades.blog.service.EsBlogService;
import top.aceofspades.blog.service.UserService;
import top.aceofspades.blog.vo.TagVO;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;
import static org.elasticsearch.search.aggregations.AggregationBuilders.terms;

/**
 * EsBlog 服务.
 *
 * @author ace
 * @version 1.0
 * @since 2018/6/16 11:40
 */
@Service
public class EsBlogServiceImpl implements EsBlogService {
    @Autowired
    private EsBlogRepository esBlogRepository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private BlogService blogService;

    @Autowired
    private UserService userService;

    @Override
    public void importAllEsBlog() {
        List<Blog> blogs = blogService.listAllBlogs();
        esBlogRepository.deleteAll();
        List<EsBlog> esBlogList = new ArrayList<>();
        for (Blog blog : blogs) {
            esBlogList.add(new EsBlog(blog));
        }

        esBlogRepository.saveAll(esBlogList);
    }

    @Override
    public void removeAllEsBlog() {
        esBlogRepository.deleteAll();
    }

    @Override
    public Long countAllEsBlog() {
        return esBlogRepository.count();
    }

    @Override
    public EsBlog updateEsBlog(EsBlog esBlog) {
        return esBlogRepository.save(esBlog);
    }

    @Override
    public EsBlog getEsBlogByBlogId(Long blogId) {
        return esBlogRepository.findByBlogId(blogId);
    }


    @Override
    public void removeEsBlog(String id) {
        esBlogRepository.deleteById(id);
    }

    @Override
    public Page<EsBlog> listNewestEsBlogs(String keyword, Pageable pageable) {
        Page<EsBlog> page;

        /*加入排序条件*/
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        if (pageable.getSort().isUnsorted()) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        }

        if (StringUtils.isNotEmpty(keyword)) {
            page = esBlogRepository.search(QueryBuilders.multiMatchQuery(keyword, "title", "tags", "content", "summary"), pageable);
        }else {
            page = esBlogRepository.findAll(pageable);
        }

        return page;
    }

    @Override
    public Page<EsBlog> listHotestEsBlogs(String keyword, Pageable pageable) {
        Page<EsBlog> page;

        /*加入排序条件*/
        Sort sort = new Sort(Sort.Direction.DESC, "readSize", "voteSize", "createTime");
        if (pageable.getSort().isUnsorted()) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        }

        if (StringUtils.isNotEmpty(keyword)) {
            page = esBlogRepository.search(QueryBuilders.multiMatchQuery(keyword, "title", "tags", "content", "summary"), pageable);
        }else {
            page = esBlogRepository.findAll(pageable);
        }

        return page;
    }

    @Override
    public Page<EsBlog> listEsBlogsByTag(String tag, Pageable pageable) {
        Page<EsBlog> page = esBlogRepository.search(QueryBuilders.matchQuery("tags", tag), pageable);
        return page;
    }

    @Override
    public Page<EsBlog> listEsBlogs(Pageable pageable) {
        return esBlogRepository.findAll(pageable);
    }

    @Override
    public List<EsBlog> listNewestEsBlogs(int count) {
        Page<EsBlog> esBlogs = this.listNewestEsBlogs("", PageRequest.of(0, count));
        return esBlogs.getContent();
    }

    @Override
    public List<EsBlog> listHotestEsBlogs(int count) {
        Page<EsBlog> esBlogs = this.listHotestEsBlogs("", PageRequest.of(0, count));
        return esBlogs.getContent();
    }

    @Override
    public List<TagVO> listTopTags(int count) {
        List<TagVO> list = new ArrayList<>();

        //查询条件
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery())
                .withSearchType(SearchType.QUERY_THEN_FETCH).withIndices("blog").withTypes("blog")
                .addAggregation(terms("tags").field("tags").order(Terms.Order.count(false)).size(count))//false 表示 降序排列
                .build();

        //聚合查询
        Aggregations aggregations = elasticsearchTemplate.query(searchQuery, new ResultsExtractor<Aggregations>() {
            @Override
            public Aggregations extract(SearchResponse response) {
                return response.getAggregations();
            }
        });

        StringTerms terms = (StringTerms) aggregations.asMap().get("tags");

        Iterator<StringTerms.Bucket> bucketIt = terms.getBuckets().iterator();
        while (bucketIt.hasNext()) {
            StringTerms.Bucket bucket = bucketIt.next();
            list.add(new TagVO(bucket.getKeyAsString(), bucket.getDocCount()));
        }
        return list;
    }

    @Override
    public List<User> listTopUsers(int count) {
        List<String> usernameList = new ArrayList<>();//存储排序后的用户账号

        //查询条件
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery())
                .withSearchType(SearchType.QUERY_THEN_FETCH).withIndices("blog").withTypes("blog")
                .addAggregation(terms("users").field("username").order(Terms.Order.count(false)).size(count)) //false 为降序
                .build();

        //聚合查询
        Aggregations aggregations = elasticsearchTemplate.query(searchQuery, new ResultsExtractor<Aggregations>() {
            @Override
            public Aggregations extract(SearchResponse response) {
                return response.getAggregations();
            }
        });

        StringTerms terms = (StringTerms) aggregations.asMap().get("users");
        Iterator<StringTerms.Bucket> bucketIt = terms.getBuckets().iterator();
        while (bucketIt.hasNext()) {
            StringTerms.Bucket bucket = bucketIt.next();
            usernameList.add(bucket.getKeyAsString());
        }

        //根据用户名。查出用户的详细信息
        List<User> list = userService.listUsersByUsernames(usernameList);

        //按照usernameList的顺序 排列 user对象
        List<User> returnList = new ArrayList<>();
        for (String username : usernameList) {
            for (User user : list) {
                if (username.equals(user.getUsername())) {
                    returnList.add(user);
                    break;
                }
            }
        }

        return returnList;
    }
}
