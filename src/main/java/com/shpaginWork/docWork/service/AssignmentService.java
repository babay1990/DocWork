package com.shpaginWork.docWork.service;

import com.shpaginWork.docWork.models.Assignment;
import com.shpaginWork.docWork.models.Users;
import com.shpaginWork.docWork.repo.AssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;

@Service
public class AssignmentService {

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private StorageService storageService;

    @Autowired
    private CustomUserDetailService userService;

    public ArrayList<Assignment> getList(){

        //находим все поручения и добавляем в arraylist
        Iterable<Assignment> all = assignmentRepository.findAll();
        ArrayList<Assignment> ar = new ArrayList<>();
        all.forEach(ar::add);

        return ar;
    }

    public String createAssignment(MultipartFile file, String executor,
                                   String assignment, String assignmentSubject) {
        //если файл не пустой, то присваиваем ему имя, сохраняем в amazon S3 и
        //в базу передаем поручение вместе с ссылкой на файл
        if(!file.isEmpty()){
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            storageService.uploadFile(file, fileName);

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

    public ArrayList<Assignment> getAssignmentListForCurrentUser(){
        Users user = userService.checkUser();

        // передаем на страницу количество невыполненных поручений
        // лист с поручениями юзеру, который передается на страницу
        ArrayList<Assignment> resulAs = new ArrayList<>();

        //находим все поручения и помещаем в лист arAs
        ArrayList<Assignment> arAs = getList();

        // вносим в лист все поручения, в которых имя пользователя совпадает с именем получателя
        // и отметка о выполнении поручения false
        for (Assignment arA : arAs) {
            if (arA.getExecutor().equals(user.getFullName()) && !arA.isStatus()) {
                resulAs.add(arA);
            }
        }

        return resulAs;
    }


}
