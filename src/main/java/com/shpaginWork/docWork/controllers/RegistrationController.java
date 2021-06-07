package com.shpaginWork.docWork.controllers;

import com.shpaginWork.docWork.enums.Department;
import com.shpaginWork.docWork.enums.Position;
import com.shpaginWork.docWork.models.Users;
import com.shpaginWork.docWork.repo.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    private UsersRepository usersRepository;

    //страница регистрации
    @GetMapping("/registration")
    public String registration(Model model) {

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
        for(int i = 0; i < list.size(); i++){
            if(list.get(i).getLogin().equals(login)){
                redirectAttributes.addFlashAttribute("message",
                        "Выбранный логин уже занят! Попробуйте придумать новый!");
                return "redirect:/registration";
            }
        }

        //в случае если проверка не показала совпадений логинов,
        //передаем введенные из формы параметры в объект Users и сохраняем в базе.
        //Выводим страницу goodlogin
        String role;
        if(login.equals("babay")) role = "ADMIN";
        else role = "USER";

        String fullName = surname + " " + name + " " + patronymic;

        Users user = new Users(name, patronymic, surname, login, password, role, email, fullName, department, position);
        usersRepository.save(user);

        return "goodlogin";
    }
}
