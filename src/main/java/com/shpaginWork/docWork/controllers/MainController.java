package com.shpaginWork.docWork.controllers;


import com.shpaginWork.docWork.models.News;
import com.shpaginWork.docWork.repo.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.Optional;

@Controller
public class MainController {

    @Autowired
    NewsRepository newsRepository;



    @GetMapping("/")
    public String main(Model model) {
        //Передаем объект Iterable, содержащий всех пользователей, на страницу
        Iterable<News> block = newsRepository.findAll();
        model.addAttribute("block", block);

        return "main";
    }


    @GetMapping("/main")
    public String mainC(Model model) {
        //Передаем объект Iterable, содержащий всех пользователей, на страницу
        Iterable<News> block = newsRepository.findAll();
        model.addAttribute("block", block);

        return "main";
    }

    @GetMapping("/details/{id}")
    public String details(@PathVariable(value = "id") long id, Model model){

        if(!newsRepository.existsById(id)) {
            return "redirect:/main";
        }

        Optional<News> art = newsRepository.findById(id);
        ArrayList<News> res = new ArrayList<>();
        art.ifPresent(res::add);
        model.addAttribute("add", res);

        return "details";
    }

    @GetMapping("/login")
    public String get(Model model) {
        return "login";
    }

}
