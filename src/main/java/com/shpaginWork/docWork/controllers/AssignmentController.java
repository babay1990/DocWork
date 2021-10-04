package com.shpaginWork.docWork.controllers;


import com.shpaginWork.docWork.service.AssignmentService;
import com.shpaginWork.docWork.service.CustomUserDetailService;
import com.shpaginWork.docWork.models.Assignment;
import com.shpaginWork.docWork.models.Users;
import com.shpaginWork.docWork.repo.AssignmentRepository;
import com.shpaginWork.docWork.repo.UsersRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

//контроллер поручений
@Controller
public class AssignmentController {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private CustomUserDetailService userService;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private AssignmentService assignmentService;

    //страница создания поручений
    @GetMapping("/createAssignment")
    public String getCreateAssignment(Model model) {
        if(userService.isAdmin()){
            model.addAttribute("isAdmin", "Панель администратора");
        }
        if(userService.isSecretary()) model.addAttribute("isSecretary", "Панель канцелярии");
        //передаем на страницу всех пользователей для поиска исполнителя
        model.addAttribute("block", usersRepository.findAll());
        return "createAssignment";
    }

    //отправка поручения
    @PostMapping("/createAssignment")
    public String postCreateAssignment(@RequestParam("file") MultipartFile[] file, @RequestParam String executor,
                                 @RequestParam String assignment, @RequestParam String assignmentSubject, RedirectAttributes redirectAttributes) {

        if(userService.checkUserByFullName(executor))
        return assignmentService.createAssignment(file, usersRepository.findByFullName(executor), assignment, assignmentSubject);
        else {
            redirectAttributes.addFlashAttribute("assignmentSubject", assignmentSubject);
            redirectAttributes.addFlashAttribute("assignment", assignment);
            redirectAttributes.addFlashAttribute("message",
                    "Выбранный исполнитель не существует. Попробуйте снова.");
            return "redirect:/createAssignment";
        }
    }

    //страница "поручения мне"
    @GetMapping("/assignmentToMe")
    public String assignmenToMe(Model model) {
        if(userService.isAdmin()){
            model.addAttribute("isAdmin", "Панель администратора");
        }
        if(userService.isSecretary()) model.addAttribute("isSecretary", "Панель канцелярии");
        //находим пользователя
        Users user = userService.checkUser();

        //лист со списком поручений, который будет отправлен на страницу
        ArrayList<Assignment> resul = new ArrayList<>();

        //находим все поручения и добавляем в arraylist
        ArrayList<Assignment> ar = assignmentService.getList();

        //вносим в отдельный arraylist все поручения, в коорых имя пользователя совпадает с именем получателя
        for (Assignment assignment : ar) {
            if (assignment.getExecutor().equals(user) && !assignment.isStatus()) {
                resul.add(assignment);
            }
        }
        //передаем arraylist на страницу
        assignmentService.sortList(resul);
        model.addAttribute("resul", resul);
        return "assignmentToMe";
    }

    //страница "мои поручения"
    @GetMapping("/myAssignment")
    public String myAssignmentt(Model model) {
        if(userService.isAdmin()){
            model.addAttribute("isAdmin", "Панель администратора");
        }
        if(userService.isSecretary()) model.addAttribute("isSecretary", "Панель канцелярии");

        //находим пользователя
        Users user = userService.checkUser();

        ArrayList<Assignment> resul = new ArrayList<>();

        //находим все поручения и добавляем в arraylist
        ArrayList<Assignment> ar = assignmentService.getList();

        //вносим в отдельный arraylist все поручения, в коорых имя пользователя совпадает с именем отправителя
        for (Assignment assignment : ar) {
            if (assignment.getSender().equals(user)) {
                resul.add(assignment);
            }
        }
        //передаем arraylist на страницу
        assignmentService.sortList(resul);
        model.addAttribute("resul", resul);
        return "myAssignment";
    }

    // архив поручений пользователя
    @GetMapping("/assignmentArchive")
    public String assignmentArchive(Model model) {
        if(userService.isAdmin()){
            model.addAttribute("isAdmin", "Панель администратора");
        }
        if(userService.isSecretary()) model.addAttribute("isSecretary", "Панель канцелярии");
        //находим пользователя
        Users user = userService.checkUser();

        ArrayList<Assignment> resul = new ArrayList<>();

        //находим все поручения и добавляем в arraylist
        ArrayList<Assignment> ar = assignmentService.getList();

        //вносим в отдельный arraylist все поручения, в коорых имя пользователя совпадает с именем отправителя
        for (Assignment assignment : ar) {
            if (assignment.getSender().equals(user) || assignment.getExecutor().equals(user)) {
                resul.add(assignment);
            }
        }
        //передаем arraylist на страницу
        assignmentService.sortList(resul);
        model.addAttribute("resul", resul);
        return "assignmentArchive";
    }

    //метод просмотра деталей поручений
    @GetMapping("/assignmentDetails/{id}")
    public String sentMessageDetails(@PathVariable(value = "id") Long id, Model model, RedirectAttributes redirectAttributes){
        if(userService.isAdmin()){
            model.addAttribute("isAdmin", "Панель администратора");
        }
        if(userService.isSecretary()) model.addAttribute("isSecretary", "Панель канцелярии");

        Users user = userService.checkUser();

        Optional<Assignment> op = assignmentRepository.findById(id);
        if(op.isPresent()) {
            Assignment assignment = op.get();
            model.addAttribute("assignment", assignment);
            model.addAttribute("linkList", assignment.getLink());

            if (user.equals(assignment.getExecutor()) && !assignment.isStatus()) {
                model.addAttribute("canDone", user);
            }
            return "assignmentDetails";
        }
        else {
            redirectAttributes.addFlashAttribute("message",
                    "Произошла ошибка. Обратитесь в службу поддержки.");
            return "redirect:/assignmentArchive";
        }
    }

    @PostMapping(value = "/assignmentDetails/{id}", params = "checkAssignment")
    public String doneAssignment(@PathVariable(value = "id") Long id, @RequestParam String comment, Model model,
                                 RedirectAttributes redirectAttributes) {

        Optional<Assignment> op = assignmentRepository.findById(id);
        if(op.isPresent()){
            Assignment assignment = op.get();

            Date date = new Date();
            assignment.setDateOfCompletion(date);
            assignment.setComment(comment);
            assignment.setStatus(true);
            assignmentRepository.save(assignment);

            return "assignmentArchive";
        }

        else {
            redirectAttributes.addFlashAttribute("message",
                    "Произошла ошибка. Обратитесь в службу поддержки.");
            return "redirect:/assignmentArchive";
        }
    }
}
