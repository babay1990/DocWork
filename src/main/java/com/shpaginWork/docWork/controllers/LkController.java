package com.shpaginWork.docWork.controllers;

import com.shpaginWork.docWork.models.Docs;
import com.shpaginWork.docWork.models.News;
import com.shpaginWork.docWork.models.Users;
import com.shpaginWork.docWork.repo.DocsRepository;
import com.shpaginWork.docWork.repo.NewsRepository;
import com.shpaginWork.docWork.repo.UsersRepository;
import com.shpaginWork.docWork.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

@Controller
public class LkController {


    @Autowired
    UsersRepository usersRepository;

    @Autowired
    DocsRepository docsRepository;

    @Autowired
    NewsRepository newsRepository;

    @Autowired
    private StorageService service;


    @GetMapping("/lk")
    public String about(Model model){

        //Находим информацию об авторизованном пользователе
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //Находим пользователя по логину и передаем на страницу всю информацию, передав объект user
        Users user = usersRepository.findByLogin(userDetails.getUsername());
        model.addAttribute("user", user);
        return "lk";
    }


    @GetMapping("/sendMessage")
    public String enterAdminUsers(Model model) {

        Iterable<Users> block = usersRepository.findAll();
        model.addAttribute("block", block);

        return "sendMessage";
    }


    @PostMapping("/sendMessage")
    public String send(@RequestParam("file") MultipartFile file, @RequestParam String recipient, @RequestParam String content, RedirectAttributes redirectAttributes) {


        if(!file.isEmpty()){
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            service.uploadFile(file, fileName);

            //Находим информацию об авторизованном пользователе
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            //Находим пользователя по логину и передаем на страницу всю информацию, передав объект user
            Users user = usersRepository.findByLogin(userDetails.getUsername());
            Docs newDoc = new Docs(user.getFullName(), recipient, content, fileName, new Date());
            docsRepository.save(newDoc);

            return "redirect:/sent";
        }
        else {
            redirectAttributes.addFlashAttribute("message",
                    "Файл не выбран! Пожалуйста, загрузите файл-сообщение!");
            return "redirect:/sendMessage";
        }

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
            if(ar.get(i).getRecipient().equals(user.getFullName())){
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
            if(ar.get(i).getSender().equals(user.getFullName())){
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


    @GetMapping("/{fileName}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable String fileName) {
        byte[] data = service.downloadFile(fileName);
        ByteArrayResource resource = new ByteArrayResource(data);
        return ResponseEntity
                .ok()
                .contentLength(data.length)
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }


    @GetMapping("/change")
    public String change(Model model){

        //Находим информацию об авторизованном пользователе
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //Находим пользователя по логину и передаем на страницу всю информацию, передав объект user
        Users user = usersRepository.findByLogin(userDetails.getUsername());
        model.addAttribute("user", user);
        return "change";
    }


    @PostMapping("/change")
    public String changeParam(@RequestParam String name, @RequestParam String patronymic, @RequestParam String surname, @RequestParam String login, @RequestParam String password, @RequestParam String email, Model model){

        //Находим информацию об авторизованном пользователе
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //Находим пользователя по логину и передаем на страницу всю информацию, передав объект user
        Users user = usersRepository.findByLogin(userDetails.getUsername());
        user.setName(name);
        user.setPatronymic(patronymic);
        user.setSurname(surname);
        user.setLogin(login);
        user.setPassword(password);
        user.setEmail(email);
        user.setFullName(name + " " + patronymic + " " + surname);
        usersRepository.save(user);
        return "redirect:/lk";

    }


}