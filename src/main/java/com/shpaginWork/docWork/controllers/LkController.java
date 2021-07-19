package com.shpaginWork.docWork.controllers;

import com.shpaginWork.docWork.service.*;
import com.shpaginWork.docWork.models.*;
import com.shpaginWork.docWork.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

//контроллер личного кабинета
@Controller
public class LkController {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private StorageService service;

    @Autowired
    private CustomUserDetailService userService;

    @Autowired
    private SentRepository sentRepository;

    @Autowired
    private InboxRepository inboxRepository;

    @Autowired
    private MessageService messageService;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private NotesService notesService;

    //главная (личный кабинет)
    @GetMapping("/")
    public String main(Model model){

        //вычисляем количество непрочитанных сообщений и передаем на страницу
        model.addAttribute("messages", messageService.getInboxListForCurrentUser().size());

        //вычисляем количество невыполненных поручений и передаем на страницу
        model.addAttribute("assigments", assignmentService.getAssignmentListForCurrentUser().size());

        //передача на страницу количества СЗ где требуется согласование или утверждение текущего user
        model.addAttribute("notesNamesCount", notesService.getNotesNamesListForCurrentUser());
        model.addAttribute("notesSignerCount", notesService.getNotesSignerListForCurrentUser());

        return "lk";
    }

    //страница с данными
    @GetMapping("/lk")
    public String lk(Model model){
        if(userService.isAdmin()){
            model.addAttribute("isAdmin", "Панель администратора");
        }

        //вычисляем количество непрочитанных сообщений и передаем на страницу
        model.addAttribute("messages", messageService.getInboxListForCurrentUser().size());

        //вычисляем количество невыполненных поручений и передаем на страницу
        model.addAttribute("assigments", assignmentService.getAssignmentListForCurrentUser().size());

        //передача на страницу количества СЗ где требуется согласование или утверждение текущего user
        model.addAttribute("notesNamesCount", notesService.getNotesNamesListForCurrentUser());
        model.addAttribute("notesSignerCount", notesService.getNotesSignerListForCurrentUser());

        return "lk";
    }

    //страница отправки сообщения
    @GetMapping("/sendMessage")
    public String getSendMessages(Model model) {
        if(userService.isAdmin()){
            model.addAttribute("isAdmin", "Панель администратора");
        }

        //передаем на страницу всех пользователей для поиска получателя
        Iterable<Users> block = usersRepository.findAll();
        model.addAttribute("block", block);
        return "sendMessage";
    }

    //отправка сообщения
    @PostMapping("/sendMessage")
    public String sendMessage(@RequestParam("file") MultipartFile file, @RequestParam String recipient,
                              @RequestParam String content, @RequestParam String messageSubject,
                              RedirectAttributes redirectAttributes) {

        if(userService.checkUserByFullName(recipient)){
            return messageService.sendMessage(file, recipient, content, messageSubject);
        }

        else {
            redirectAttributes.addFlashAttribute("message",
                    "Выбранного получателя не существует. Пожалуйста, введите корректные данные.");
            return "redirect:/sendMessage";
        }
    }

    //страница "входящие"
    @GetMapping("/inbox")
    public String inbox(Model model) {
        if(userService.isAdmin()){
            model.addAttribute("isAdmin", "Панель администратора");
        }

        //находим пользователя
        Users user = userService.checkUser();

        //лист с входящими, который будет передан на страницу
        ArrayList<Inbox> resul = new ArrayList<>();

        //находим все документы и добавляем во временный arraylist
        ArrayList<Inbox> ar = messageService.getInboxList();

        //вносим в лист resul все документы, в коорых имя пользователя совпадает с именем получателя
        for (Inbox inbox : ar) {
            if (inbox.getRecipient().equals(user.getFullName())) {
                resul.add(inbox);
            }
        }
        //передаем arraylist на страницу

        messageService.sortInboxList(resul);
        model.addAttribute("resul", resul);
        return "inbox";
    }

    //страница "отправленные"
    @GetMapping("/sent")
    public String sent(Model model) {
        if(userService.isAdmin()){
            model.addAttribute("isAdmin", "Панель администратора");
        }

        //находим пользователя
        Users user = userService.checkUser();

        // лист с исходящими, который будет передан на страницу
        ArrayList<Sent> resul = new ArrayList<>();

        //находим все документы и добавляем во временный arraylist
        ArrayList<Sent> ar = messageService.getSentList();

        //вносим в отдельный arraylist все документы, в коорых имя пользователя совпадает с именем отправителя
        for (Sent sent : ar) {
            if (sent.getSender().equals(user.getFullName())) {
                resul.add(sent);
            }
        }
        //передаем arraylist на страницу
        messageService.sortSentList(resul);
        model.addAttribute("resul", resul);
        return "sent";
    }

    //страница со списком пользователей
    @GetMapping("/lkUsers")
    public String getLkUsers(Model model) {
        if(userService.isAdmin()){
            model.addAttribute("isAdmin", "Панель администратора");
        }

        //Передаем объект Iterable, содержащий всех пользователей, на страницу
        Iterable<Users> block = usersRepository.findAll();
        model.addAttribute("block", block);
        return "lkUsers";
    }

    //сохарение файла
    @GetMapping("/{fileName}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable String fileName) {
        byte[] data = service.downloadFile(fileName);
        ByteArrayResource resource = new ByteArrayResource(data);
        return ResponseEntity
                .ok()
                .contentLength(data.length)
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }

    //метод удаления входящих сообщений
    @PostMapping(value = "/inbox", params = "bar3")
    public String deleteInboxMessage(@RequestParam String content, Model model) {

        Inbox inbox = inboxRepository.findByContent(content);
        inboxRepository.delete(inbox);
        return "redirect:/inbox";
    }

    //метод удаления исходящих сообщений
    @PostMapping(value = "/sent", params = "bar4")
    public String deleteSentMessage(@RequestParam String content, Model model) {

        Sent sent = sentRepository.findByContent(content);
        sentRepository.delete(sent);
        return "redirect:/sent";
    }

    //метод просмотра данных отправителя
    @GetMapping("/userDetails/{sender}")
    public String senderDetails(@PathVariable(value = "sender") String sender, Model model){
        if(userService.isAdmin()){
            model.addAttribute("isAdmin", "Панель администратора");
        }

        Users user = usersRepository.findByFullName(sender);
        if(user == null) {
            model.addAttribute("message",
                    "Данный пользователь не существует. Если такого не можеть быть, обратитесь в службу поддержки.");
            return "errorPage";
        }
        else {
            model.addAttribute("user", user);
            return "userDetails";
        }
    }

    //метод просмотра деталей входящих сообщений
    @GetMapping("/messageDetails/{id}")
    public String inboxMessageDetails(@PathVariable(value = "id") Long id, Model model, RedirectAttributes redirectAttributes){
        if(userService.isAdmin()){
            model.addAttribute("isAdmin", "Панель администратора");
        }

        Optional<Inbox> op = inboxRepository.findById(id);
        if(op.isPresent()){
            Inbox message = op.get();
            message.setCheckMessage(true);
            inboxRepository.save(message);
            model.addAttribute("message", message);
            return "messageDetails";
        }
        else {
            redirectAttributes.addFlashAttribute("message",
                    "Произошла ошибка. Обратитесь в службу поддержки.");
            return "redirect:/inbox";
        }
    }

    //метод просмотра деталей исходящих сообщений
    @GetMapping("/sentMessageDetails/{id}")
    public String sentMessageDetails(@PathVariable(value = "id") Long id, Model model, RedirectAttributes redirectAttributes){
        if(userService.isAdmin()){
            model.addAttribute("isAdmin", "Панель администратора");
        }

        Optional<Sent> op = sentRepository.findById(id);
        if(op.isPresent()){
            Sent message = op.get();
            model.addAttribute("message", message);
            return "sentMessageDetails";
        }
        else{
            redirectAttributes.addFlashAttribute("message",
                    "Произошла ошибка. Обратитесь в службу поддержки.");
            return "redirect:/sent";
        }
    }

    //метод поиска по имени
    @PostMapping(value = "/lkUsers", params = "searchByUserName")
    public String searchByUserName(@RequestParam String name, Model model){

        ArrayList<Users> resul = userService.searchByUserFullName(name);
        model.addAttribute("resul", resul);
        return "resulSearch";
    }

}

