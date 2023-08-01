package com.example.honjarang.domain.post.service;


import com.example.honjarang.domain.post.dto.*;
import com.example.honjarang.domain.DateTimeUtils;
import com.example.honjarang.domain.post.entity.Comment;
import com.example.honjarang.domain.post.entity.LikePost;
import com.example.honjarang.domain.post.entity.Post;
import com.example.honjarang.domain.post.exception.*;
import com.example.honjarang.domain.post.repository.CommentRepository;
import com.example.honjarang.domain.post.repository.LikePostRepository;
import com.example.honjarang.domain.post.repository.PostRepository;
import com.example.honjarang.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    private final CommentRepository commentRepository;

    private final LikePostRepository likePostRepository;

    @Transactional
    public Long createPost(PostCreateDto postCreateDto, User user) {
        return postRepository.save(postCreateDto.toEntity(user)).getId();
    }

    @Transactional
    public void deletePost(long id, User user) {
        Post post = postRepository.findById(id).orElseThrow(() ->
                new PostNotFoundException("존재하지 않는 게시글입니다."));

        if (!Objects.equals(post.getUser().getId(), user.getId())) {
            throw new InvalidUserException("작성자만 삭제할 수 있습니다.");
        }
        postRepository.deleteById(id);
    }

    @Transactional
    public void updatePost(Long id, PostUpdateDto postUpdateDto, User user) {
        Post post = postRepository.findById(id).orElseThrow(() ->
                new PostNotFoundException("존재하지 않는 게시글입니다."));
        if (!Objects.equals(post.getUser().getId(), user.getId())) {
            throw new InvalidUserException("작성자만 수정할 수 있습니다.");
        }
        post.update(postUpdateDto);
    }

    @Transactional
    public void createComment(Long id, CommentCreateDto commentCreateDto, User user) {
        Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException("존재하지 않는 게시글입니다."));
        if (commentCreateDto.getContent().isEmpty()) {
            throw new ContentEmptyException("댓글을 작성하세요.");
        }
        commentRepository.save(commentCreateDto.toEntity(post, user));
    }


    @Transactional(readOnly = true)
    public List<PostListDto> getPostList(int page, String keyword) {
        Pageable pageable = PageRequest.of(page -1, 15);
        return postRepository.findAllByTitleContainingIgnoreCaseOrderByIsNoticeDescIdDesc(keyword, pageable)
                .stream()
                .map(post -> toPostListDto(post))
                .toList();
    }

    @Transactional
    public PostDto getPost(long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException("존재하지 않는 게시글입니다."));
        postRepository.increaseViews(id);
        post.increaseViews();
        return toPostDto(post);

    }

    @Transactional
    public void togglePostLike(Long id, User user) {
        Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException("존재하지 않는 게시글입니다."));
        if (likePostRepository.countByPostIdAndUserId(id, user.getId()) == 0) {
            likePostRepository.save(LikePost.builder().post(post).user(user).build());
        } else {
            likePostRepository.deleteByPostIdAndUserId(id, user.getId());
        }
    }

    public PostListDto toPostListDto(Post post) {
        return new PostListDto(
                post.getId(),
                post.getUser().getId(),
                post.getTitle(),
                post.getCategory(),
                post.getContent(),
                post.getViews(),
                post.getIsNotice(),
                DateTimeUtils.formatLocalDateTime(post.getCreatedAt())
        );
    }

    private PostDto toPostDto(Post post) {
        return new PostDto(
                post.getId(),
                post.getUser().getId(),
                post.getTitle(),
                post.getCategory(),
                post.getContent(),
                post.getUser().getNickname(),
                post.getViews(),
                post.getIsNotice(),
                DateTimeUtils.formatLocalDateTime(post.getCreatedAt())
        );
    }

    private CommentListDto toCommentListDto(Comment comment) {
        return new CommentListDto(
                comment.getId(),
                comment.getContent(),
                comment.getUser().getId(),
                comment.getUser().getNickname(),
                DateTimeUtils.formatLocalDateTime(comment.getCreatedAt())
        );
    }

    @Transactional
    public void deleteComment(Long id, User user) {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new CommentNotFoundException("존재하지 않는 댓글 입니다."));
        if (!Objects.equals(comment.getUser().getId(), user.getId())) {
            throw new InvalidUserException("작성자만 삭제할 수 있습니다.");
        }
        commentRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<CommentListDto> getCommentList(Long id) {
        postRepository.findById(id).orElseThrow(() -> new PostNotFoundException("게시글이 존재하지 않습니다."));
        return commentRepository.findAllByPostId(id).stream()
                .map(this::toCommentListDto)
                .toList();

    }

}
