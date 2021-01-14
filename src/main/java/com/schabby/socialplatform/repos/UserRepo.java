package com.schabby.socialplatform.repos;

import org.springframework.data.repository.CrudRepository;
import com.schabby.socialplatform.models.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends CrudRepository<User, Long> {
    User findById(long id);
}
