package com.shpaginWork.docWork.controllers;

import com.shpaginWork.docWork.models.Docs;
import com.shpaginWork.docWork.models.News;
import com.shpaginWork.docWork.models.Users;
import com.shpaginWork.docWork.repo.DocsRepository;
import com.shpaginWork.docWork.repo.NewsRepository;
import com.shpaginWork.docWork.repo.UsersRepository;
import com.shpaginWork.docWork.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

@Controller
public class LkController {

    private final StorageService storageService;

    @Autowired
    public LkController(StorageService storageService) {
        this.storageService = storageService;
    }

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    DocsRepository docsRepository;

    @Autowired
    NewsRepository newsRepository;


    @GetMapping("/lk")
    public String about(Model model){

        //Находим информацию об авторизованном пользователе
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //Находим пользователя по логину и передаем на страницу всю информацию, передав объект user
        Users user = usersRepository.findByLogin(userDetails.getUsername());
        model.addAttribute("user", user);
        model.addAttribute("url", user.getUrl());
        return "lk";
    }



    @PostMapping("/lk")
    public String avatar(@RequestParam("file") MultipartFile file) {

        storageService.store(file);


        //Находим информацию об авторизованном пользователе
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //Находим пользователя по логину и передаем на страницу всю информацию, передав объект user
        Users user = usersRepository.findByLogin(userDetails.getUsername());

        String link = System.getProperty("catalina.home")+ File.separator + "files" + File.separator + file.getOriginalFilename();
        user.setUrl(link);
        usersRepository.save(user);

        return "redirect:/lk";
    }






    @GetMapping("/sendMessage")
    public String enterAdminUsers(Model model) {

        Iterable<Users> block = usersRepository.findAll();
        model.addAttribute("block", block);

        return "sendMessage";
    }


    @PostMapping("/sendMessage")
    public String send(@RequestParam("file") MultipartFile file, @RequestParam String recipient, @RequestParam String content) {

        storageService.store(file);


        //Находим информацию об авторизованном пользователе
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //Находим пользователя по логину и передаем на страницу всю информацию, передав объект user
        Users user = usersRepository.findByLogin(userDetails.getUsername());

        String link = System.getProperty("catalina.home")+ File.separator + "files" + File.separator + file.getOriginalFilename();
        Docs newDoc = new Docs(user.getLogin(), recipient, content, link, new Date());
        docsRepository.save(newDoc);

        return "redirect:/sent";
    }

    @GetMapping("/inbox")
    public String in(Model model) {

        //Находим информацию об авторизованном пользователе
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //Находим пользователя по логину и передаем на страницу всю информацию, передав объект user
        Users user = usersRepository.findByLogin(userDetails.getUsername());

        Iterable<Docs> all = docsRepository.findAll();
        ArrayList<Docs> ar = new ArrayList<>();
        ArrayList<Docs> resul = new ArrayList<>();
        all.forEach(ar::add);

        for(int i = 0; i < ar.size(); i++){
            if(ar.get(i).getRecipient().equals(user.getLogin())){
                resul.add(ar.get(i));
            }
        }
        model.addAttribute("resul", resul);

        return "inbox";
    }

    @GetMapping("/sent")
    public String sent(Model model) {

        //Находим информацию об авторизованном пользователе
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //Находим пользователя по логину и передаем на страницу всю информацию, передав объект user
        Users user = usersRepository.findByLogin(userDetails.getUsername());

        Iterable<Docs> all = docsRepository.findAll();
        ArrayList<Docs> ar = new ArrayList<>();
        ArrayList<Docs> resul = new ArrayList<>();
        all.forEach(ar::add);

        for(int i = 0; i < ar.size(); i++){
            if(ar.get(i).getSender().equals(user.getLogin())){
                resul.add(ar.get(i));
            }
        }
        model.addAttribute("resul", resul);

        return "sent";
    }

    @GetMapping("/addNews")
    public String addNews(Model model) {
        return "addNews";
    }

    @PostMapping("/addNews")
    public String addArticle(@RequestParam String title, @RequestParam String annotation, @RequestParam String text, Model model){

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Users user = usersRepository.findByLogin(userDetails.getUsername());

        News news = new News(title, annotation, text, user.getLogin(), user.getName() + " " + user.getPatronymic() + " " + user.getSurname());
        newsRepository.save(news);

        return "redirect:/main";

    }

    @GetMapping("/lkUsers")
    public String enterLkUsers(Model model) {

        //Передаем объект Iterable, содержащий всех пользователей, на страницу
        Iterable<Users> block = usersRepository.findAll();
        model.addAttribute("block", block);
        return "lkUsers";
    }


}