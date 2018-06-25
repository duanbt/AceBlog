package top.aceofspades.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import top.aceofspades.blog.domain.Vote;

/**
 * Vote Repository接口.
 * @author ace
 * @version 1.0
 * @since 2018/6/13 11:18
 */
public interface VoteRepository extends JpaRepository<Vote, Long> {
}
