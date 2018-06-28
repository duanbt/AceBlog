package top.aceofspades.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import top.aceofspades.blog.domain.User;
import top.aceofspades.blog.domain.Vote;
import top.aceofspades.blog.service.BlogService;
import top.aceofspades.blog.service.VoteService;
import top.aceofspades.blog.vo.Response;

import java.util.Optional;

/**
 * 点赞 控制器.
 *
 * @author ace
 * @version 1.0
 * @since 2018/6/13 11:57
 */
@Controller
@RequestMapping("/votes")
public class VoteController {
    @Autowired
    private BlogService blogService;

    @Autowired
    private VoteService voteService;


    /**
     * 点赞
     *
     * @param blogId
     * @return
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")//验证已登录
    public ResponseEntity<Response> createVote(Long blogId) {
        Vote vote = blogService.createVote(blogId);
        return ResponseEntity.ok().body(new Response(true, "点赞成功", vote));
    }

    /**
     * 删除点赞
     *
     * @param id     vote 对象 id
     * @param blogId
     * @return
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<Response> delete(@PathVariable("id") Long id, Long blogId) {
        Optional<Vote> optionalVote = voteService.getVoteById(id);
        User voteUser = null;
        if (optionalVote.isPresent()) {
            voteUser = optionalVote.get().getUser();
        } else {
            return ResponseEntity.ok().body(new Response(false, "不存在该点赞"));
        }

        //判断操作用户必须是点赞的所有者，才能删除
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal != null && principal.getUsername().equals(voteUser.getUsername())) {//操作者是点赞人
            blogService.removeVote(blogId, id);
        } else {//操作者不是点赞人
            return ResponseEntity.ok().body(new Response(false, "没有权限"));
        }
        return ResponseEntity.ok().body(new Response(true, "取消点赞成功"));
    }

}
