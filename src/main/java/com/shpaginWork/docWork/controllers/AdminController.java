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
    UsersRepository usersRepository;

    @Autowired
    NewsRepository newsRepository;

    @Autowired
    CustomUserDetailService userService;

    //Вход на страницу администратора
    //На ней отображаем количество зарегестрированных пользователей и количество размещенных новостей
    @GetMapping("/admin")
    public String getAdmin(Model model){
        if(userService.isAdmin()){
            model.addAttribute("isAdmin", "Панель администратора");
        }

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
        if(userService.isAdmin()){
            model.addAttribute("isAdmin", "Панель администратора");
        }

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
        if(userService.isAdmin()){
            model.addAttribute("isAdmin", "Панель администратора");
        }
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

    //страница "добавление новости"
    @GetMapping("/addNews")
    public String enterAddNews(Model model) {
        if(userService.isAdmin()){
            model.addAttribute("isAdmin", "Панель администратора");
        }
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

        //находим пользователя
        Users user = userService.checkUser();

        //передаем на страницу объект user
        model.addAttribute("user", user);
        return "change";
    }

    //изменение личных данных пользователя
    @PostMapping("/change")
    public String changeParam(@RequestParam String name, @RequestParam String patronymic,
                              @RequestParam String surname, @RequestParam String login,
                              @RequestParam String password, @RequestParam String email, Model model){

        //находим пользователя
        Users user = userService.checkUser();

        //передаем параметры, полученные со страницы и сохраняем измененные данные в базе данных
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
