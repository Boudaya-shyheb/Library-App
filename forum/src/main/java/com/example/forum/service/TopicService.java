package com.example.forum.service;

import com.example.forum.entity.Topic;
import com.example.forum.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;

    public Topic createTopic(Topic topic) {
        return topicRepository.save(topic);
    }

    public List<Topic> getAllTopics() {
        return topicRepository.findAll();
    }

    public Topic getTopicById(Long id) {
        return topicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Topic not found"));
    }

    public Topic updateTopic(Long id, Topic updatedTopic) {
        Topic topic = getTopicById(id);
        topic.setTitle(updatedTopic.getTitle());
        topic.setCreatedBy(updatedTopic.getCreatedBy());
        return topicRepository.save(topic);
    }


    public void deleteTopic(Long id) {
        topicRepository.deleteById(id);
    }
}