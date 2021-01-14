package com.schabby.socialplatform.repos;

import org.springframework.data.repository.CrudRepository;
import com.schabby.socialplatform.models.Post;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepo extends CrudRepository<Post, Long> {
    Post findById(long id);
}