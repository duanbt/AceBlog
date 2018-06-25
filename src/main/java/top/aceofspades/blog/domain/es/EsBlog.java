package top.aceofspades.blog.domain.es;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import top.aceofspades.blog.domain.Blog;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

/**
 * EsBlog 文档类.
 *
 * @author ace
 * @version 1.0
 * @since 2018/6/15 19:41
 */
@Document(indexName = "blog", type = "blog", shards = 3, replicas = 0)
public class EsBlog {

    @Id  // 主键
    private String id;
    @Field(type = FieldType.Long)
    private Long blogId; // Blog 实体的 id
    @Field(type = FieldType.Text,searchAnalyzer = "ik_smart", analyzer = "ik_smart")
    private String title;
    @Field(type = FieldType.Text,searchAnalyzer = "ik_smart", analyzer = "ik_smart")
    private String summary;
    @Field(type = FieldType.Text,searchAnalyzer = "ik_smart", analyzer = "ik_smart")
    private String content;
    @Field(type = FieldType.Keyword, index = false)  // 不做全文检索字段
    private String username;
    @Field(type = FieldType.Text, index = false)  // 不做全文检索字段
    private String avatar;
    @Field(type = FieldType.Date, index = false)  // 不做全文检索字段
    private Timestamp createTime;
    @Field(type = FieldType.Integer, index = false)  // 不做全文检索字段
    private Integer readSize = 0; // 访问量、阅读量
    @Field(type = FieldType.Integer, index = false)  // 不做全文检索字段
    private Integer voteSize = 0;  // 点赞量
    @Field(type = FieldType.Keyword)
    private List<String> tags;  // 标签

    protected EsBlog() {
    }

    public EsBlog(Long blogId, String title, String summary, String content,
                  String username, String avatar, Timestamp createTime,
                  Integer readSize,  Integer voteSize, String tags) {
        this.blogId = blogId;
        this.title = title;
        this.summary = summary;
        this.content = content;
        this.username = username;
        this.avatar = avatar;
        this.createTime = createTime;
        this.readSize = readSize;
        this.voteSize = voteSize;
        String[] tagArray = StringUtils.split(tags, ",");
        this.tags = Arrays.asList(tagArray);
    }

    public EsBlog(Blog blog) {
        this.blogId = blog.getId();
        this.title = blog.getTitle();
        this.summary = blog.getSummary();
        this.content = blog.getContent();
        this.username = blog.getUser().getUsername();
        this.avatar = blog.getUser().getAvatar();
        this.createTime = blog.getCreateTime();
        this.readSize = blog.getReadSize();
        this.voteSize = blog.getVoteSize();
        String[] tagArray = StringUtils.split(blog.getTags(), ",");
        this.tags = Arrays.asList(tagArray);
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getBlogId() {
        return blogId;
    }

    public void setBlogId(Long blogId) {
        this.blogId = blogId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Integer getReadSize() {
        return readSize;
    }

    public void setReadSize(Integer readSize) {
        this.readSize = readSize;
    }

    public Integer getVoteSize() {
        return voteSize;
    }

    public void setVoteSize(Integer voteSize) {
        this.voteSize = voteSize;
    }


    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "EsBlog{" +
                "id='" + id + '\'' +
                ", blogId=" + blogId +
                ", title='" + title + '\'' +
                ", summary='" + summary + '\'' +
                '}';
    }

    public void update(Blog blog) {
        this.blogId = blog.getId();
        this.title = blog.getTitle();
        this.summary = blog.getSummary();
        this.content = blog.getContent();
        this.username = blog.getUser().getUsername();
        this.avatar = blog.getUser().getAvatar();
        this.createTime = blog.getCreateTime();
        this.readSize = blog.getReadSize();
        this.voteSize = blog.getVoteSize();
        String[] tagArray = StringUtils.split(blog.getTags(), ",");
        this.tags = Arrays.asList(tagArray);
    }
}
