package com.schabby.socialplatform.controllers;

import com.google.common.hash.Hashing;
import com.schabby.socialplatform.models.Post;
import com.schabby.socialplatform.models.User;
import com.schabby.socialplatform.repos.PostRepo;
import com.schabby.socialplatform.repos.UserRepo;
import java.io.IOException;
import static java.lang.System.currentTimeMillis;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Controller
public class WebController {
    
    @Autowired
    UserRepo userRepo;
    
    @Autowired
    PostRepo postRepo;
    
    Boolean exists = false;
    
    public List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    
    //Sign up
    @GetMapping("/sign_up")
    public String inputForm(Model model) {
        model.addAttribute("user", new User());
        return "sign_up";
    }
    
    @PostMapping("/sign_up")
    public String submitForm(Model model, @ModelAttribute User user) {
        List<User> users;
        users = (ArrayList) userRepo.findAll();
        boolean inUse = false;
        
        if (user.getUsername().equals("") || user.getPassword().equals("")) return ("/sign_up");
        
        for (User u : users) {
            System.out.println(u.getUsername() + " " + user.getUsername());
            if (u.getUsername().equals(user.getUsername())) inUse = true;
        }
        
        if (inUse == true) {
            model.addAttribute("inUse", inUse);
            model.addAttribute(user);
            return "sign_up";
        } else {
            String password = user.getPassword();
            String sha256hex = Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString();
            userRepo.save(new User(user.getUsername(), sha256hex, user.getName(), user.getAge(), "/css/blank_profile_picture_00.jpg", false));
            model.addAttribute(user);
            return "submitted";
        }
    }
    
    @GetMapping("/")
    public String login(Model model) {
        model.addAttribute("user", new User());
        return "index";
    }
    
    //Sign in
    @PostMapping("/")
    public String submitLogin(@ModelAttribute User user) {
        
        ArrayList<User> users = new ArrayList<>();
        
        userRepo.findAll().forEach(e -> users.add(e));
        
        String userPasswordSHA256Hex = Hashing.sha256().hashString(user.getPassword(), StandardCharsets.UTF_8).toString();
        
        for (User u : users) {
            
            if (u.getUsername().equals(user.getUsername()) && u.getPassword().equals(userPasswordSHA256Hex)) {
                
                u.setOnline(true);
                userRepo.save(u);
                
                return ("redirect:/newsfeed/" + u.getId());
            }
        }

        return "index";
        
    }
    
    //Newsfeed
    @GetMapping("/newsfeed/{id}")
    public String newsfeed(Model model, @PathVariable("id") long id) {
        
        ArrayList<Post> posts = (ArrayList) postRepo.findAll(); 
        ArrayList<User> users = (ArrayList) userRepo.findAll();
        
        User[] onlineUsers = users.stream().filter(u -> u.isOnline() == true).toArray(User[]::new);
        
        Post[] orderedPosts = new Post[posts.size()];
        
        if (!posts.isEmpty()) {
            long maxOrdinal = 0;
            for (int x = 0; x < posts.size(); x++) {
                if (maxOrdinal < posts.get(x).getId()) maxOrdinal = posts.get(x).getId();
            }
            
            long missing = maxOrdinal - posts.size();
            
            for (int x = 0; x < posts.size(); x++) {
                long ordinal = posts.get(x).getId();
                if (ordinal > missing) ordinal -= missing;
                orderedPosts[(int) ((long) posts.size() - ordinal)] = posts.get(x);
            }
        }
        
        User user = userRepo.findById(id);
        Post post = new Post();
        
        model.addAttribute("posts", orderedPosts);
        model.addAttribute("user", user);
        model.addAttribute("post", post);
        model.addAttribute("users", onlineUsers);
        return "newsfeed";
    }
    
    @PostMapping("/posting/{user_id}")
    public ModelAndView posting(@ModelAttribute Post post, @PathVariable("user_id") long user_id) {
        
        User u = userRepo.findById(user_id);
        Date date = new Date(currentTimeMillis());
        
        String dateToString = DateFormat.getDateTimeInstance(DateFormat.LONG,DateFormat.SHORT).format(date);
        
        post.setDate(dateToString);
        post.setUser(u);
        
        postRepo.save(post);
        
        return new ModelAndView("redirect:/newsfeed/" + u.getId());
    }
    
    @GetMapping("/deletePost/{post_id}")
    public ModelAndView deletePost(@PathVariable("post_id") long post_id) {
        
        Post post = postRepo.findById(post_id);
        
        long userId = post.getUser().getId();
        
        postRepo.delete(post);
        
        return new ModelAndView("redirect:/newsfeed/" + userId);
    }
    
    @GetMapping("/profil/{myId}/{profilId}")
    public String profil(Model model, @PathVariable("myId") long myId, @PathVariable("profilId") long profilId, @RequestParam(required=false) String url) {
        
        if (myId == profilId) {
            
            User user = userRepo.findById(myId);
            List<User> users;
            users = (ArrayList) userRepo.findAll();
            
            User[] onlineUsers = users.stream().filter(u -> u.isOnline() == true).toArray(User[]::new);

            if (url != null) {
                user.setPicture(url);
                userRepo.save(user);
                user = userRepo.findById(myId);
            }

            model.addAttribute("user", user);
            model.addAttribute("users", onlineUsers);

            return ("profil");
            
        } else {
            
            User me = userRepo.findById(myId);
            User user = userRepo.findById(profilId);
            List<User> users;
            users = (ArrayList) userRepo.findAll();
            
            User[] onlineUsers = users.stream().filter(u -> u.isOnline() == true).toArray(User[]::new);
            
            model.addAttribute("me", me);
            model.addAttribute("user", user);
            model.addAttribute("users", onlineUsers);
        
            return ("other_profil");
        }
    }
    
    
    
    @PostMapping("/save")
    @ResponseStatus(value = HttpStatus.OK)
    public void saveData(@ModelAttribute User user) {
        
        User u = new User();
        long i = user.getId();
        
        u = userRepo.findById((int) i);
        
        if (user.getUsername() != null) {
            u.setUsername(user.getUsername());
            userRepo.save(u);
        } else if (user.getPassword() != null) {
            String passwordSHA256Hex = Hashing.sha256().hashString(user.getPassword(), StandardCharsets.UTF_8).toString();
            u.setPassword(passwordSHA256Hex);
            userRepo.save(u);
        } else if (user.getName() != null) {
            u.setName(user.getName());
            userRepo.save(u);
        } else if (user.getAge() != 0) {
            u.setAge(user.getAge());
            userRepo.save(u);
        }
        
    }
    
    @GetMapping("/like/{postId}/{userId}")
    public ModelAndView like(@PathVariable("postId") long postId, @PathVariable("userId") long userId) {
        
        Post p = (Post) postRepo.findById(postId);
        long[] likes = p.getLikes();
        boolean isLiked = false;
        
        for (int x = 0; x < likes.length; x++) {
            if (likes[x] == userId) isLiked = true;
        }
        
        if (isLiked == false) {
            p.addLike(userId);
            postRepo.save(p);
        }
        
        return new ModelAndView("redirect:/newsfeed/" + userId);
    }
    
    @GetMapping("/logout")
    public String logout(@RequestParam long id) {
        
        User user = userRepo.findById(id);
        user.setOnline(false);
        
        userRepo.save(user);
        
        return "redirect:/";
    }
    
    @GetMapping("/leave/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public void leave(@PathVariable("id") long id) {
        
        User user = userRepo.findById(id);
        user.setOnline(false);
        
        userRepo.save(user);
    }
    
    
    @GetMapping("/chatWith/{from}/{to}/{roomId}")
    public String connectToChat(@PathVariable("from") long from, @PathVariable("to") long to, 
                                @PathVariable("roomId") long roomId, Model model) {
        
        String inviter = userRepo.findById(from).getUsername();
        String invitee = userRepo.findById(to).getUsername();
        
        List<User> users;
        users = (ArrayList) userRepo.findAll();
        
        model.addAttribute("from", inviter);
        model.addAttribute("to", invitee);
        model.addAttribute("users", users);
        
        model.addAttribute("roomId", roomId);
        
        return ("chat"); 
    }
    
    @RequestMapping(value = "/checkIn", consumes = MediaType.ALL_VALUE)
    public SseEmitter subscribe() {

        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);

        try {
            sseEmitter.send(SseEmitter.event().name("checkedIn"));
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(ChatController.class.getName()).log(Level.SEVERE, null, ex);
            emitters.remove(sseEmitter);
            System.out.println("sseEmitter terminated");
        }

        sseEmitter.onCompletion(() -> {
            emitters.remove(sseEmitter);
            System.out.println("sseEmitter terminated");
        }); 

        emitters.add(sseEmitter);

        return sseEmitter;

    }

    @PostMapping("/invite/{from}/{to}")
    @ResponseBody
    public void invite(@PathVariable long from, @PathVariable long to) {
        
        Object[] data = new Object[4];
        long randomRoomId = new Random().nextLong();
        
        data[0] = (long) from;
        data[1] = (long) to;
        data[2] = randomRoomId;
        data[3] = userRepo.findById(from).getName();

        emitters.forEach((emitter) -> {
            try {
                emitter.send(SseEmitter.event().name("invitation").data(data));
            } catch (IOException ex) {
                emitters.remove(emitter);
            }
        });
    }
    
}