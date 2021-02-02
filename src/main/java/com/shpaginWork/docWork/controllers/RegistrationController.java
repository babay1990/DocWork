package com.shpaginWork.docWork.controllers;

import com.shpaginWork.docWork.models.Users;
import com.shpaginWork.docWork.repo.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;

@Controller
public class RegistrationController {
    @Autowired
    private UsersRepository usersRepository;

    @GetMapping("/registration")
    public String registr(Model model) {
        return "registration";
    }


    //метод обработки формы регистрации
    @PostMapping("/registration")
    public String addRegistration(@RequestParam String name, @RequestParam String patronymic, @RequestParam String surname, @RequestParam String login, @RequestParam String password, @RequestParam String email, Model model){

        //Первоначально делается проверка, недопускающая повторения уже существующего логина
        //через Iterable находим все пользователей и заносим их в ArrayList
        Iterable<Users> it = usersRepository.findAll();
        ArrayList<Users> list = new ArrayList<>();
        it.forEach(list :: add);

        //для каждого логина пользователя из листа проводим проверку
        //сравниваем логин существующих пользователей и логин
        //переданный из формы. В случае совпадения выводим страницу badlogin
        //на которой просим пользователя ввести другие данные
        for(int i = 0; i < list.size(); i++){
            if(list.get(i).getLogin().equals(login)){
                return "badlogin";
            }
        }

        //в случае если проверка не показала совпадений логинов,
        //передаем введенные из формы параметры в объект Users и сохраняем в базе.
        //Выводим страницу goodlogin
        String role;
        if(login.equals("babay")) role = "ADMIN";
        else role = "USER";

        String fullName = name + " " + patronymic + " " + surname;

        Users user = new Users(name, patronymic, surname, login, password, role, email, fullName);
        usersRepository.save(user);
        return "goodlogin";

    }
}
