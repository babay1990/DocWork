package com.shpaginWork.docWork.controllers;

import com.shpaginWork.docWork.models.News;
import com.shpaginWork.docWork.models.Users;
import com.shpaginWork.docWork.repo.NewsRepository;
import com.shpaginWork.docWork.repo.UsersRepository;
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
    UsersRepository usersRepository;

    @Autowired
    NewsRepository newsRepository;

    //Вход на страницу администратора
    //На ней отображаем количество зарегестрированных пользователей и количество размещенных новостей
    @GetMapping("/admin")
    public String getAdmin(Model model){

        //помещаем всех пользователей в arraylist и находим его размер
        Iterable<Users> all = usersRepository.findAll();
        ArrayList<Users> ar = new ArrayList<>();
        all.forEach(ar::add);
        int usersSize = ar.size();

        //помещаем все новости в arraylist и находим его размер
        Iterable<News> allNews = newsRepository.findAll();
        ArrayList<News> arNews = new ArrayList<>();
        allNews.forEach(arNews::add);
        int newsSize = arNews.size();

        //передаем на страницу полученные размеры
        model.addAttribute("usersSize", usersSize);
        model.addAttribute("newsSize", newsSize);

        return "admin";
    }

    //Вход на страницу adminUsers
    //На странице отображаются данные всех зарегестрированных пользователей
    @GetMapping("/adminUsers")
    public String getAdminUsers(Model model) {

        //Передаем объект Iterable, содержащий всех пользователей, на страницу
        Iterable<Users> block = usersRepository.findAll();
        model.addAttribute("block", block);
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
        Iterable<News> block = newsRepository.findAll();
        model.addAttribute("block", block);
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
}
