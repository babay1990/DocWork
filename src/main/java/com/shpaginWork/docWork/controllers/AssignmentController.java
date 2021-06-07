package com.shpaginWork.docWork.controllers;

import com.shpaginWork.docWork.enums.Department;
import com.shpaginWork.docWork.models.Sent;
import com.shpaginWork.docWork.service.AssignmentService;
import com.shpaginWork.docWork.service.CustomUserDetailService;
import com.shpaginWork.docWork.models.Assignment;
import com.shpaginWork.docWork.models.Users;
import com.shpaginWork.docWork.repo.AssignmentRepository;
import com.shpaginWork.docWork.repo.UsersRepository;
import com.shpaginWork.docWork.service.StorageService;
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
    private StorageService service;

    @Autowired
    private CustomUserDetailService userService;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private AssignmentService assignmentService;

    //страница создания поручений
    @GetMapping("/createAssignment")
    public String getCreateAssignment(Model model) {

        //передаем на страницу всех пользователей для поиска исполнителя
        Iterable<Users> block = usersRepository.findAll();
        model.addAttribute("block", block);
        return "createAssignment";
    }

    //отправка поручения
    @PostMapping("/createAssignment")
    public String postCreateAssignment(@RequestParam("file") MultipartFile file, @RequestParam String executor,
                                 @RequestParam String assignment, @RequestParam String assignmentSubject,
                                 RedirectAttributes redirectAttributes) {

        //если файл не пустой, то присваиваем ему имя, сохраняем в amazon S3 и
        //в базу передаем поручение вместе с ссылкой на файл
        if(!file.isEmpty()){
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            service.uploadFile(file, fileName);

            Users user = userService.checkUser();

            Date date = new Date();

            Assignment newAssignment = new Assignment(user.getFullName(), executor, assignment, fileName, date, assignmentSubject);

            assignmentRepository.save(newAssignment);

            return "redirect:/lk";
        }
        //если файл пустой, то отправляем пользователю поручение без файла
        else {

            Users user = userService.checkUser();

            Date date = new Date();

            Assignment newAssignment = new Assignment(user.getFullName(), executor, assignment, date, assignmentSubject);

            assignmentRepository.save(newAssignment);

            return "redirect:/lk";
        }
    }


    //страница "поручения мне"
    @GetMapping("/assignmentToMe")
    public String assignmenToMe(Model model) {

        //находим пользователя
        Users user = userService.checkUser();

        //лист со списком поручений, который будет отправлен на страницу
        ArrayList<Assignment> resul = new ArrayList<>();

        //находим все поручения и добавляем в arraylist
        ArrayList<Assignment> ar = assignmentService.getList();


        //вносим в отдельный arraylist все поручения, в коорых имя пользователя совпадает с именем получателя
        for(int i = 0; i < ar.size(); i++){
            if(ar.get(i).getExecutor().equals(user.getFullName()) && !ar.get(i).isStatus()){
                resul.add(ar.get(i));
            }
        }
        //передаем arraylist на страницу
        model.addAttribute("resul", resul);
        return "assignmentToMe";
    }

    //страница "мои поручения"
    @GetMapping("/myAssignment")
    public String myAssignmentt(Model model) {

        //находим пользователя
        Users user = userService.checkUser();

        ArrayList<Assignment> resul = new ArrayList<>();

        //находим все поручения и добавляем в arraylist
        ArrayList<Assignment> ar = assignmentService.getList();

        //вносим в отдельный arraylist все поручения, в коорых имя пользователя совпадает с именем отправителя
        for(int i = 0; i < ar.size(); i++){
            if(ar.get(i).getSender().equals(user.getFullName())){
                resul.add(ar.get(i));
            }
        }
        //передаем arraylist на страницу
        model.addAttribute("resul", resul);
        return "myAssignment";
    }

    // архив поручений пользователя
    @GetMapping("/assignmentArchive")
    public String assignmentArchive(Model model) {

        //находим пользователя
        Users user = userService.checkUser();

        ArrayList<Assignment> resul = new ArrayList<>();

        //находим все поручения и добавляем в arraylist
        ArrayList<Assignment> ar = assignmentService.getList();

        //вносим в отдельный arraylist все поручения, в коорых имя пользователя совпадает с именем отправителя
        for(int i = 0; i < ar.size(); i++){
            if(ar.get(i).getSender().equals(user.getFullName()) || ar.get(i).getExecutor().equals(user.getFullName())){
                resul.add(ar.get(i));
            }
        }
        //передаем arraylist на страницу
        model.addAttribute("resul", resul);
        return "assignmentArchive";
    }

    //метод просмотра деталей поручений
    @GetMapping("/assignmentDetails/{id}")
    public String sentMessageDetails(@PathVariable(value = "id") Long id, Model model){

        Users user = userService.checkUser();

        Optional<Assignment> op = assignmentRepository.findById(id);
        Assignment assignment = op.get();
        model.addAttribute("assignment", assignment);

        if(user.getFullName().equals(assignment.getExecutor()) && !assignment.isStatus()) {
            model.addAttribute("canDone", user);
        }


        return "assignmentDetails";
    }

    @PostMapping(value = "/assignmentDetails/{id}", params = "checkAssignment")
    public String doneAssignment(@PathVariable(value = "id") Long id, @RequestParam String comment, Model model) {

        Optional<Assignment> op = assignmentRepository.findById(id);
        Assignment assignment = op.get();

        Date date = new Date();
        assignment.setDateOfCompletion(date);
        assignment.setComment(comment);
        assignment.setStatus(true);
        assignmentRepository.save(assignment);

        return "assignmentArchive";
    }

}
