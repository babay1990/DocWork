package com.shpaginWork.docWork.controllers;


import com.shpaginWork.docWork.service.CustomUserDetailService;

import com.shpaginWork.docWork.models.News;
import com.shpaginWork.docWork.repo.NewsRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//контроллер главной страницы
@Controller
public class NewsController {

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private CustomUserDetailService userService;

    //главная страница
    @GetMapping("/news")
    public String news(Model model) throws IOException {
        if(userService.isAdmin()){
            model.addAttribute("isAdmin", "Панель администратора");
        }
        if(userService.isSecretary()) model.addAttribute("isSecretary", "Панель канцелярии");

        //Передаем объект Iterable, содержащий все новости, на страницу
        Iterable<News> block = newsRepository.findAll();
        model.addAttribute("block", block);

        List<String> list = new ArrayList<>();

        Document doc = Jsoup.connect("https://yandex.ru/").get();

        Elements el = doc.getElementsByAttributeValue("class", "inline-stocks__value");

        el.forEach(ell ->{

            String element = ell.child(1).text();
            list.add(element);
        });

        String usd = list.get(0);
        String eur = list.get(1);

        model.addAttribute("usd", usd);
        model.addAttribute("eur", eur);

        list.clear();

        return "main";
    }

    //страница детального описания новости
    @GetMapping("/details/{id}")
    public String details(@PathVariable(value = "id") long id, Model model){
        if(userService.isAdmin()){
            model.addAttribute("isAdmin", "Панель администратора");
        }
        if(userService.isSecretary()) model.addAttribute("isSecretary", "Панель канцелярии");

        if(!newsRepository.existsById(id)) {
            return "redirect:/main";
        }

        //находим новость по id и передаем данные на страницу
        Optional<News> art = newsRepository.findById(id);
        ArrayList<News> res = new ArrayList<>();
        art.ifPresent(res::add);
        model.addAttribute("add", res);

        return "details";
    }
}
