package com.example.forum.controller;

import com.example.forum.entity.Topic;
import com.example.forum.service.TopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forum/topics")
@RequiredArgsConstructor
@CrossOrigin
public class TopicController {

    private final TopicService topicService;

    // CREATE
    @PostMapping
    public Topic create(@RequestBody Topic topic) {
        return topicService.createTopic(topic);
    }

    // READ ALL
    @GetMapping
    public List<Topic> getAll() {
        return topicService.getAllTopics();
    }

    // READ BY ID
    @GetMapping("/{id}")
    public Topic getById(@PathVariable Long id) {
        return topicService.getTopicById(id);
    }

    // UPDATE
    @PutMapping("/{id}")
    public Topic update(@PathVariable Long id, @RequestBody Topic topic) {
        return topicService.updateTopic(id, topic);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        topicService.deleteTopic(id);
    }
}