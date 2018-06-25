package top.aceofspades.blog.service;

import top.aceofspades.blog.domain.Vote;

import java.util.Optional;

/**
 * Vote 服务接口.
 * @author ace
 * @version 1.0
 * @since 2018/6/13 11:29
 */
public interface VoteService {

    /**
     * 根据id获取Vote
     * @param id
     * @return
     */
    Optional<Vote> getVoteById(Long id);

    /**
     * 删除Vote
     * @param id
     */
    void removeVote(Long id);
}
