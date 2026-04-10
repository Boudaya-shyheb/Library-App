package com.example.forum.controller;

import com.example.forum.entity.Post;
import com.example.forum.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forum/posts")
@RequiredArgsConstructor
@CrossOrigin
public class PostController {

    private final PostService postService;

    // CREATE
    @PostMapping("/topic/{topicId}")
    public Post create(@PathVariable Long topicId,
                       @RequestBody Post post) {
        return postService.createPost(topicId, post);
    }

    // READ ALL
    @GetMapping
    public List<Post> getAll() {
        return postService.getAllPosts();
    }

    // READ BY TOPIC
    @GetMapping("/topic/{topicId}")
    public List<Post> getByTopic(@PathVariable Long topicId) {
        return postService.getPostsByTopic(topicId);
    }

    // UPDATE
    @PutMapping("/{id}")
    public Post update(@PathVariable Long id,
                       @RequestBody Post post) {
        return postService.updatePost(id, post);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        postService.deletePost(id);
    }
}