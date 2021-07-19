package com.shpaginWork.docWork.controllers;

import com.shpaginWork.docWork.enums.Department;
import com.shpaginWork.docWork.enums.Position;
import com.shpaginWork.docWork.models.Users;
import com.shpaginWork.docWork.repo.UsersRepository;
import com.shpaginWork.docWork.service.CustomUserDetailService;
import com.shpaginWork.docWork.service.TechnicalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;

//контроллер регистрации
@Controller
public class RegistrationController {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private CustomUserDetailService userService;

    @Autowired
    private TechnicalService technicalService;

    //страница регистрации
    @GetMapping("/registration")
    public String registration(Model model) {
        if(userService.isAdmin()){
            model.addAttribute("isAdmin", "Панель администратора");
        }

        Department[] departmentList = Department.getList();
        model.addAttribute("departmentList", departmentList);

        Position[] positionList = Position.getList();
        model.addAttribute("positionList", positionList);

        return "registration";
    }


    //метод обработки формы регистрации
    @PostMapping("/registration")
    public String addNewUser(@RequestParam String name, @RequestParam String patronymic,
                             @RequestParam String surname, @RequestParam String login, @RequestParam String password,
                             @RequestParam String email, @RequestParam Department department, @RequestParam Position position,
                             Model model, RedirectAttributes redirectAttributes){

        //Первоначально делается проверка, недопускающая повторения уже существующего логина
        //через Iterable находим все пользователей и заносим их в ArrayList
        Iterable<Users> it = usersRepository.findAll();
        ArrayList<Users> list = new ArrayList<>();
        it.forEach(list :: add);

        //для каждого логина пользователя из листа проводим проверку
        //сравниваем логин существующих пользователей и логин
        //переданный из формы. В случае совпадения выводим на страницу сообщение
        //в котором просим пользователя ввести другие данные
        for (Users users : list) {
            if (users.getLogin().equals(login)) {
                redirectAttributes.addFlashAttribute("message",
                        "Выбранный логин уже занят! Попробуйте придумать новый!");
                return "redirect:/registration";
            }
        }

        //проверка на наличие цифр в ФИО
        if(technicalService.checkForNumbers(name) || technicalService.checkForNumbers(patronymic) || technicalService.checkForNumbers(surname)){
            model.addAttribute("message",
                    "Неправильно введено ФИО. ФИО не должно содержать цифр. Попробуйте еще раз!");
            return "errorPage";
        }

        //в случае если проверка не показала совпадений логинов,
        //передаем введенные из формы параметры в объект Users и сохраняем в базе.
        //Выводим страницу goodlogin
        String role;
        if(login.equals("admin")) role = "ADMIN";
        else role = "USER";

        String fullName = surname + " " + name + " " + patronymic;

        Users user = new Users(name, patronymic, surname, login, bCryptPasswordEncoder.encode(password), role, email, fullName, department, position);
        usersRepository.save(user);

        return "goodlogin";
    }

    //страница авторизации
    @GetMapping("/login")
    public String get(Model model) {
        return "login";
    }

    //страница авторизации
    @GetMapping("/info")
    public String info(Model model) {
        return "info";
    }






    //страница ознакомительной регистрации
    @GetMapping("/registrationExample")
    public String registrationExample(Model model) {

        Department[] departmentList = Department.getList();
        model.addAttribute("departmentList", departmentList);

        Position[] positionList = Position.getList();
        model.addAttribute("positionList", positionList);

        return "registrationExample";
    }



    //регистрация с ознакомительной страницы
    @PostMapping("/registrationExample")
    public String postRegistrationExamle(@RequestParam String name, @RequestParam String patronymic,
                             @RequestParam String surname, @RequestParam String login, @RequestParam String password,
                             @RequestParam String email, @RequestParam Department department, @RequestParam Position position,
                             Model model, RedirectAttributes redirectAttributes){

        //Первоначально делается проверка, недопускающая повторения уже существующего логина
        //через Iterable находим все пользователей и заносим их в ArrayList
        Iterable<Users> it = usersRepository.findAll();
        ArrayList<Users> list = new ArrayList<>();
        it.forEach(list :: add);

        //для каждого логина пользователя из листа проводим проверку
        //сравниваем логин существующих пользователей и логин
        //переданный из формы. В случае совпадения выводим на страницу сообщение
        //в котором просим пользователя ввести другие данные
        for (Users users : list) {
            if (users.getLogin().equals(login)) {
                redirectAttributes.addFlashAttribute("message",
                        "Выбранный логин уже занят! Попробуйте придумать новый!");
                return "redirect:/registrationExample";
            }
        }

        //проверка на наличие цифр в ФИО
        if(technicalService.checkForNumbers(name) || technicalService.checkForNumbers(patronymic) || technicalService.checkForNumbers(surname)){
            model.addAttribute("message",
                    "Неправильно введено ФИО. ФИО не должно содержать цифр. Попробуйте еще раз!");
            return "errorPage";
        }

        //в случае если проверка не показала совпадений логинов,
        //передаем введенные из формы параметры в объект Users и сохраняем в базе.
        //Выводим страницу goodlogin
        String role = "ADMIN";

        String fullName = surname + " " + name + " " + patronymic;

        Users user = new Users(name, patronymic, surname, login, bCryptPasswordEncoder.encode(password), role, email, fullName, department, position);
        usersRepository.save(user);

        return "goodlogin";
    }
}
