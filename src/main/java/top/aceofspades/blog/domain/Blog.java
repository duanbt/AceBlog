package top.aceofspades.blog.domain;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;

/**
 * Blog 实体.
 *
 * @author ace
 * @version 1.0
 * @since 2018/6/6 17:40
 */
@Entity
public class Blog {
    public static final String BY_TIME = "new";
    public static final String BY_HOT = "hot";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "标题不能为空")
    @Size(min = 2, max = 50, message = "标题必须为2-50个字符")
    @Column(nullable = false, length = 50)
    private String title;//标题

    @NotEmpty(message = "摘要不能为空")
    @Size(min = 2, max = 300, message = "摘要过长或过短")
    @Column(nullable = false)
    private String summary;//摘要

    @Lob//大对象，映射 MySQL 的 Long Text 类型
    @Basic(fetch = FetchType.LAZY)//懒加载
    @NotEmpty(message = "正文不能为空")
    @Size(min = 2, message = "正文过短")
    @Column(nullable = false)
    private String content;//md格式的正文

    @Column(nullable = false)
    @CreationTimestamp //Hibernate注解, 由数据库自动创建时间
    private Timestamp createTime;//创建时间

    @Column
    private Integer readSize = 0;//访问量

    @Column
    private Integer voteSize = 0;//点赞量

    @Column(length = 100)
    private String tags;//标签

    @OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_id")
    private List<Vote> votes;

    @OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "catalog_id")
    private Catalog catalog;//分类

    protected Blog() {
    }

    public Blog(Catalog catalog) {
        this.catalog = catalog;
    }

    public Blog(String title, String summary, String content) {
        this.title = title;
        this.summary = summary;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public List<Vote> getVotes() {
        return votes;
    }

    public void setVotes(List<Vote> votes) {
        this.votes = votes;
        this.voteSize = votes.size();
    }

    /**
     * 点赞
     * @param vote 点赞对象，需持有点赞人user对象
     * @return 是否已点赞（true：重复点赞，false：第一次点赞）
     */
    public boolean addVote(Vote vote){
        //判断重复
        boolean isExist = false;
        for (Vote v : votes) {
            if(v.getUser().getId().equals(vote.getUser().getId())){
                isExist = true;
                break;
            }
        }

        if(!isExist){
            this.votes.add(vote);
            this.voteSize = this.votes.size();
        }
        return isExist;
    }

    /**
     * 取消点赞
     * @param voteId
     */
    public void removeVote(Long voteId){
        Iterator<Vote> iterator = this.votes.iterator();
        while (iterator.hasNext()){
            Vote vote = iterator.next();
            if(vote.getId().equals(voteId)){
                iterator.remove();
                break;
            }
        }
        this.voteSize = this.votes.size();
    }

    public Catalog getCatalog() {
        return catalog;
    }

    public void setCatalog(Catalog catalog) {
        this.catalog = catalog;
    }
}
