package com.shpaginWork.docWork.controllers;

import com.shpaginWork.docWork.models.News;
import com.shpaginWork.docWork.models.Users;
import com.shpaginWork.docWork.repo.NewsRepository;
import com.shpaginWork.docWork.repo.UsersRepository;
import com.shpaginWork.docWork.service.CustomUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;

//контроллер для действий на панели администратора
@Controller
public class AdminController {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private CustomUserDetailService userService;

    //Вход на страницу администратора
    //На ней отображаем количество зарегестрированных пользователей и количество размещенных новостей
    @GetMapping("/admin")
    public String getAdminInfo(Model model){
        if(userService.isAdmin()){
            model.addAttribute("isAdmin", "Панель администратора");
        }
        if(userService.isSecretary()) model.addAttribute("isSecretary", "Панель канцелярии");
        //передаем на страницу количество пользователей и новостей
        model.addAttribute("usersSize", usersRepository.findAll().size());
        model.addAttribute("newsSize", newsRepository.findAll().size());
        return "admin";
    }

    //Вход на страницу adminUsers
    //На странице отображаются данные всех зарегестрированных пользователей
    @GetMapping("/adminUsers")
    public String getAdminUsers(Model model) {
        if(userService.isAdmin()){
            model.addAttribute("isAdmin", "Панель администратора");
        }
        if(userService.isSecretary()) model.addAttribute("isSecretary", "Панель канцелярии");

        //Передаем на страницу всех пользователей
        model.addAttribute("block", usersRepository.findAll());
        return "adminUsers";
    }

    //Метод для обработки формы удаления пользователя
    @PostMapping(value = "/adminUsers", params = "deleteUser")
    public String deleteUser(@RequestParam String login, Model model) {

        //находим пользователя в базе по полученному логину и удаляем
        Users user = usersRepository.findByLogin(login);
        usersRepository.delete(user);
        return "redirect:/adminUsers";
    }

    //метод для обработки формы присвоения роли админа
    @PostMapping(value = "/adminUsers", params = "setAdminRole")
    public String setAdminRole(@RequestParam String login, Model model) {

        //по переданному из формы логину находим пользователя и присваиваем ему роль ADMIN
        Users user = usersRepository.findByLogin(login);
        user.setRole("ADMIN");
        usersRepository.save(user);
        return "redirect:/adminUsers";
    }

    //страница для просмотра размещенных новостей
    @GetMapping("/adminNews")
    public String adminNews(Model model) {
        if(userService.isAdmin()){
            model.addAttribute("isAdmin", "Панель администратора");
        }
        if(userService.isSecretary()) model.addAttribute("isSecretary", "Панель канцелярии");

        model.addAttribute("block", newsRepository.findAll());
        return "adminNews";
    }

    //Метод для обработки формы удаления новости
    @PostMapping(value = "/adminNews", params = "deleteNews")
    public String deleteNews (@RequestParam String title, Model model) {

        //находим новость по полученному названию и удаляем из базы
        News news = newsRepository.findByTitle(title);
        newsRepository.delete(news);
        return "redirect:/adminNews";
    }

    //страница "добавление новости"
    @GetMapping("/addNews")
    public String enterAddNews(Model model) {
        if(userService.isAdmin()){
            model.addAttribute("isAdmin", "Панель администратора");
        }
        if(userService.isSecretary()) model.addAttribute("isSecretary", "Панель канцелярии");
        return "addNews";
    }

    //добавление новости
    @PostMapping("/addNews")
    public String addNews(@RequestParam String title, @RequestParam String annotation, @RequestParam String text, Model model){

        //находим пользователя
        Users user = userService.checkUser();

        //передаем параметры, полученные со страницу и записываем их в базу данных новостей
        News news = new News(title, annotation, text, user.getLogin(), user.getName() + " " + user.getPatronymic() + " " + user.getSurname());
        newsRepository.save(news);
        return "redirect:/main";
    }

    //страница изменения личных данных
    @GetMapping("/change")
    public String change(Model model){
        if(userService.isAdmin()){
            model.addAttribute("isAdmin", "Панель администратора");
        }
        if(userService.isSecretary()) model.addAttribute("isSecretary", "Панель канцелярии");

        //находим пользователя
        Users user = userService.checkUser();

        //передаем на страницу объект user
        model.addAttribute("user", user);
        return "change";
    }

    //метод поиска по имени
    @PostMapping(value = "/adminUsers", params = "searchByUserName")
    public String searchByUserName(@RequestParam String name, Model model){
        if(userService.isAdmin()){
            model.addAttribute("isAdmin", "Панель администратора");
        }
        if(userService.isSecretary()) model.addAttribute("isSecretary", "Панель канцелярии");

        ArrayList<Users> resul = userService.searchByUserFullName(name);
        model.addAttribute("admin", "admin");
        model.addAttribute("resul", resul);
        return "resulSearch";
    }


}
