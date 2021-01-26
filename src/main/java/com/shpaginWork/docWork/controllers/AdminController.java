package com.shpaginWork.docWork.controllers;

import com.shpaginWork.docWork.models.Docs;
import com.shpaginWork.docWork.models.News;
import com.shpaginWork.docWork.models.Users;
import com.shpaginWork.docWork.repo.NewsRepository;
import com.shpaginWork.docWork.repo.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.mail.MessagingException;
import java.util.ArrayList;

@Controller
public class AdminController {

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    NewsRepository newsRepository;

    @Autowired
    public JavaMailSender emailSender;

    //Вход на страницу администратора
    @GetMapping("/admin")
    public String enter(Model model){

        Iterable<Users> all = usersRepository.findAll();
        ArrayList<Users> ar = new ArrayList<>();
        all.forEach(ar::add);
        int usersSize = ar.size();

        Iterable<News> allNews = newsRepository.findAll();
        ArrayList<News> arNews = new ArrayList<>();
        allNews.forEach(arNews::add);
        int newsSize = arNews.size();

        model.addAttribute("usersSize", usersSize);
        model.addAttribute("newsSize", newsSize);


        return "admin";
    }

    //Вход на страницу adminUsers
    @GetMapping("/adminUsers")
    public String enterAdminUsers(Model model) {

        //Передаем объект Iterable, содержащий всех пользователей, на страницу
        Iterable<Users> block = usersRepository.findAll();
        model.addAttribute("block", block);
        return "adminUsers";
    }



    //Метод для обработки формы удаления пользователя
    @PostMapping(value = "/adminUsers", params = "bar2")
    public String changUsersParametrs(@RequestParam String login, Model model) {

        Users user = usersRepository.findByLogin(login);
        usersRepository.delete(user);
        return "redirect:/adminUsers";
    }

    //метод для обработки формы присвоения роли админа
    @PostMapping(value = "/adminUsers", params = "bar")
    public String changeRole(@RequestParam String login, Model model) {

        //по переданному из формы логину находим пользователя и присваиваем ему роль ADMIN
        Users user = usersRepository.findByLogin(login);
        user.setRole("ADMIN");
        usersRepository.save(user);
        return "redirect:/adminUsers";
    }


    @GetMapping("/adminNews")
    public String adminNews(Model model) {
        Iterable<News> block = newsRepository.findAll();
        model.addAttribute("block", block);
        return "adminNews";
    }


    //Метод для обработки формы удаления новости
    @PostMapping(value = "/adminNews", params = "bar3")
    public String deleteNews (@RequestParam String title, Model model) {

        News news = newsRepository.findByTitle(title);
        newsRepository.delete(news);
        return "redirect:/adminNews";
    }





    @GetMapping("/sendEmail")
    public String send(Model model){
        return "sendEmail";
    }

    //Метод обработки формы. Передаем тему, текст письма и адрес отправления
    @PostMapping(value = "/sendEmail", params = "bar4")
    public String sendMail(@RequestParam String theme, @RequestParam String text, @RequestParam String email, Model model) throws MessagingException {

        //Создаем сообщение
        SimpleMailMessage message = new SimpleMailMessage();

        //задаем адрес отправителя
        message.setFrom("shpaginjava@gmail.com");
        //задаем адрес получателя из формы, тему и текст письма
        message.setTo(email);
        message.setSubject(theme);
        message.setText(text);

        //Отправляем сообщение, возвращаемся на страницу администратора
        emailSender.send(message);
        return "redirect:/admin";
    }








    @GetMapping("/test")
    public String test(Model model){

        Iterable<Users> block = usersRepository.findAll();
        model.addAttribute("block", block);

        return "test";
    }

}
