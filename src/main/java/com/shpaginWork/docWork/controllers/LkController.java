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
    private NewsRepository newsRepository;

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


    //страница с данными
    @GetMapping("/lk")
    public String lk(Model model){

        Users user = userService.checkUser();

        //лист, в который будут добавлены все непрочитанные сообщения
        ArrayList<Inbox> resul = new ArrayList<>();

        // находим все входящие сообщения и добавляем в лист ar
        ArrayList<Inbox> ar = messageService.getInboxList();

        //вносим в resul все документы, в которых имя пользователя совпадает с именем получателя
        //и в которых отметка о прочитанном сообщении false
        for(int i = 0; i < ar.size(); i++){
            if(ar.get(i).getRecipient().equals(user.getFullName()) && !ar.get(i).isCheckMessage()){
                resul.add(ar.get(i));
            }
        }

        //вычисляем размер листа и передаем значение счетчика на страницу
        int inboxCount = resul.size();
        model.addAttribute("messages", inboxCount);



        // передаем на страницу количество невыполненных поручений
        // лист с поручениями юзеру, который передается на страницу
        ArrayList<Assignment> resulAs = new ArrayList<>();

        //находим все поручения и помещаем в лист arAs
        ArrayList<Assignment> arAs = assignmentService.getList();

        // вносим в лист все поручения, в которых имя пользователя совпадает с именем получателя
        // и отметка о выполнении поручения false
        for(int i = 0; i < arAs.size(); i++){
            if(arAs.get(i).getExecutor().equals(user.getFullName()) && !arAs.get(i).isStatus()){
                resulAs.add(arAs.get(i));
            }
        }

        //вычисляем размер листа и передаем значение счетчика на страницу
        int assigments = resulAs.size();
        model.addAttribute("assigments", assigments);


        //передача на страницу СЗ где требуется согласование или утверждение

        // счетчики
        int notesNamesCount = 0;
        int notesSignerCount = 0;

        // лист list будет передан на страницу как список СЗ, в которых присутствует пользователь
        ArrayList<Notes> list = new ArrayList<>();

        //получаем список всех служебных записок
        ArrayList<Notes> arNotes = notesService.getList();

        for(int i = 0; i < arNotes.size(); i++) {

            // заполняем лист checks значениями согласовано/не согласовано конкретной СЗ
            ArrayList<Boolean> checks = new ArrayList<>(arNotes.get(i).getMap().values());

            // заполняем лист allNames именами согласующих СЗ
            ArrayList<String> allNames = notesService.getNamesList(arNotes.get(i));

            // теперь есть лист с чеками checks и лист с именами согласующих allNames
            // если имя согласующего совпадает с именем юзера и отметка согласующего false
            // то увеличиваем счетчик "на согласование"
            for(int j = 0; j < allNames.size(); j++) {
                if(allNames.get(j).equals(user.getFullName()) && !checks.get(j)) {
                    notesNamesCount++;
                }
            }

            // если имя подписанта совпадает с именем юзера, все отметки согласования не содержат false
            // и отметка утверждения документа false, то увеличиваем счетчик "на утверждение"
            if(arNotes.get(i).getSigner().equals(user.getFullName()) && !arNotes.get(i).isCheck() && !checks.contains(false)) {
                notesSignerCount++;
            }
        }

        // передаем значения счетчиков на страницу
        model.addAttribute("notesNamesCount", notesNamesCount);
        model.addAttribute("notesSignerCount", notesSignerCount);

        return "lk";
    }

    //страница отправки сообщения
    @GetMapping("/sendMessage")
    public String enterSendMessages(Model model) {

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

        //если файл не пустой, то присваиваем ему имя, сохраняем в amazon S3 и в базе данных
        //в базу передаем отправителя, получателя, описание сообщения, имя файла и дату отправки
        if(!file.isEmpty()){
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            service.uploadFile(file, fileName);

            Users user = userService.checkUser();

            Date date = new Date();

            Inbox inbox = new Inbox(user.getFullName(), recipient, content, fileName, date, false, messageSubject);
            Sent sent = new Sent(user.getFullName(), recipient, content, fileName, date, messageSubject);

            inboxRepository.save(inbox);
            sentRepository.save(sent);

            return "redirect:/sent";
        }
        //если файл пустой, то отправляем пользователю сообщение на страницу
        else {

            Users user = userService.checkUser();

            Date date = new Date();

            Inbox inbox = new Inbox(user.getFullName(), recipient, content, date, false, messageSubject);
            Sent sent = new Sent(user.getFullName(), recipient, content, date, messageSubject);

            inboxRepository.save(inbox);
            sentRepository.save(sent);

            return "redirect:/sent";
        }
    }

    //страница "входящие"
    @GetMapping("/inbox")
    public String inbox(Model model) {

        //находим пользователя
        Users user = userService.checkUser();

        //лист с входящими, который будет передан на страницу
        ArrayList<Inbox> resul = new ArrayList<>();

        //находим все документы и добавляем во временный arraylist
        ArrayList<Inbox> ar = messageService.getInboxList();

        //вносим в лист resul все документы, в коорых имя пользователя совпадает с именем получателя
        for(int i = 0; i < ar.size(); i++){
            if(ar.get(i).getRecipient().equals(user.getFullName())){
                resul.add(ar.get(i));
            }
        }
        //передаем arraylist на страницу
        model.addAttribute("resul", resul);
        return "inbox";
    }

    //страница "отправленные"
    @GetMapping("/sent")
    public String sent(Model model) {

        //находим пользователя
        Users user = userService.checkUser();

        // лист с исходящими, который будет передан на страницу
        ArrayList<Sent> resul = new ArrayList<>();

        //находим все документы и добавляем во временный arraylist
        ArrayList<Sent> ar = messageService.getSentList();

        //вносим в отдельный arraylist все документы, в коорых имя пользователя совпадает с именем отправителя
        for(int i = 0; i < ar.size(); i++){
            if(ar.get(i).getSender().equals(user.getFullName())){
                resul.add(ar.get(i));
            }
        }
        //передаем arraylist на страницу
        model.addAttribute("resul", resul);
        return "sent";
    }

    //страница "добавление новости"
    @GetMapping("/addNews")
    public String enterAddNews(Model model) {
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

    //страница со списком пользователей
    @GetMapping("/lkUsers")
    public String enterLkUsers(Model model) {

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

    //страница изменения личных данных
    @GetMapping("/change")
    public String change(Model model){

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
    public String details(@PathVariable(value = "sender") String sender, Model model){

        Users user = usersRepository.findByFullName(sender);
        model.addAttribute("user", user);
        return "userDetails";
    }

    //метод просмотра деталей входящих сообщений
    @GetMapping("/messageDetails/{id}")
    public String messageDetails(@PathVariable(value = "id") Long id, Model model){

        Optional<Inbox> op = inboxRepository.findById(id);
        Inbox message = op.get();
        message.setCheckMessage(true);
        inboxRepository.save(message);
        model.addAttribute("message", message);
        return "messageDetails";
    }

    //метод просмотра деталей исходящих сообщений
    @GetMapping("/sentMessageDetails/{id}")
    public String sentMessageDetails(@PathVariable(value = "id") Long id, Model model){

        Optional<Sent> op = sentRepository.findById(id);
        Sent message = op.get();
        model.addAttribute("message", message);
        return "sentMessageDetails";
    }

    //метод поиска по имени
    @PostMapping(value = "/lkUsers", params = "searchByUserName")
    public String searchByUserName(@RequestParam String name, Model model){

        ArrayList<Users> resul = userService.searchByUserFullName(name);
        model.addAttribute("resul", resul);
        return "resulSearch";
    }

}

