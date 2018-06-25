package top.aceofspades.blog.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.aceofspades.blog.domain.Vote;
import top.aceofspades.blog.repository.VoteRepository;
import top.aceofspades.blog.service.VoteService;

import java.util.Optional;

/**
 * Vote 服务实现.
 * @author ace
 * @version 1.0
 * @since 2018/6/13 11:31
 */
@Service
public class VoteServiceImpl implements VoteService {
    @Autowired
    private VoteRepository voteRepository;

    @Override
    public Optional<Vote> getVoteById(Long id) {
        return voteRepository.findById(id);
    }

    @Override
    public void removeVote(Long id) {
        voteRepository.deleteById(id);
    }
}
