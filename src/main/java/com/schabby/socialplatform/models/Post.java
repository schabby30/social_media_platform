package com.schabby.socialplatform.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@SequenceGenerator(name="seq", initialValue=1, allocationSize=100)
@Table(name="posts") 
public class Post implements Serializable {
    
    @Id
    @GeneratedValue
    private long id;
    
    private String text;
    
    //@Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private String date;
    
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="User_id")
    private User User;
    
    private long[] likes = new long[20];
    
    
    public Post() {
    }

    public Post(String text, String date, User user) {
        this.text = text;
        this.date = date;
        this.User = user;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public User getUser() {
        return User;
    }

    public void setUser(User user) {
        this.User = user;
    }

    public long[] getLikes() {
        return likes;
    }

    public void setLikes(long[] likes) {
        this.likes = likes;
    }

    public void addLike(long user) {
        int x = 0;
        while (this.likes[x] != 0) x++;
        if (x < 20) this.likes[x] = user;
    }
    
    public void removeLike(long user) {
        long[] arr = new long[this.likes.length - 2];
        for (int x = 0; x < this.likes.length; x++) {
            if (this.likes[x] == user) x++;
            if (x < this.likes.length) arr[x] = this.likes[x];
        }
        this.likes = arr;
    }
    
}
