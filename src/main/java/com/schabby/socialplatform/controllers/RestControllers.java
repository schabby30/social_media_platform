package com.schabby.socialplatform.controllers;

import com.schabby.socialplatform.models.Post;
import com.schabby.socialplatform.models.User;
import com.schabby.socialplatform.repos.PostRepo;
import com.schabby.socialplatform.repos.UserRepo;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestControllers {
    
    @Autowired
    UserRepo userRepo;
    
    @Autowired
    PostRepo postRepo;
    
    @GetMapping("/read")
    public List<User> readData() {
        ArrayList<User> users = new ArrayList<>();
        
        userRepo.findAll().forEach(e -> users.add(e));
        return users;
    }
    
    @GetMapping("/read2")
    public List<Post> readPosts() {
        return (List<Post>) postRepo.findAll();    
    }
    
    @GetMapping("/userNameById/{id}")
    public String userNameById(@PathVariable("id") long id) {
        String userName = userRepo.findById(id).getUsername();
        return userName;
    }
    
    @GetMapping("/deleteById/{id}")
    public List<User> deleteById(@PathVariable("id") long id) {
        User user = userRepo.findById(id);
        userRepo.deleteById(user.getId());
        return (List<User>) userRepo.findAll();
    }
}