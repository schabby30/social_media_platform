package com.schabby.socialplatform.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@SequenceGenerator(name="seq", initialValue=1, allocationSize=100)
@Table(name="users") 
public class User implements Serializable {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY, generator="seq")
    private Long id;
    
    private String username;
    private String password;
    private String name;
    private int age;
    private String picture;
    private boolean online;
    
    @JsonIgnore
    @OneToMany(mappedBy = "User")
    private Collection<Post> posts = new ArrayList<Post>();

    public User() {
    }

    public User(String username, String password, String name, int age, String picture, boolean online) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.age = age;
        this.picture = picture;
        this.online = online;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Collection<Post> getPosts() {
        return posts;
    }

    public void setPosts(Collection<Post> posts) {
        this.posts = posts;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
    
    
    
}
