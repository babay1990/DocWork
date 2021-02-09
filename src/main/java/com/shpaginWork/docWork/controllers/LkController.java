package com.shpaginWork.docWork.controllers;

import com.shpaginWork.docWork.CustomUserDetailService;
import com.shpaginWork.docWork.models.Docs;
import com.shpaginWork.docWork.models.News;
import com.shpaginWork.docWork.models.Users;
import com.shpaginWork.docWork.repo.DocsRepository;
import com.shpaginWork.docWork.repo.NewsRepository;
import com.shpaginWork.docWork.repo.UsersRepository;
import com.shpaginWork.docWork.service.StorageService;
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

import java.util.ArrayList;
import java.util.Date;

//контроллер личного кабинета
@Controller
public class LkController {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private DocsRepository docsRepository;

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private StorageService service;

    @Autowired
    private CustomUserDetailService userService;

    //страница с личными данными
    @GetMapping("/lk")
    public String lk(Model model){

        //передаем на страницу объект user
        Users user = userService.checkUser();
        model.addAttribute("user", user);
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
    public String sendMessage(@RequestParam("file") MultipartFile file, @RequestParam String recipient, @RequestParam String content, RedirectAttributes redirectAttributes) {

        //если файл не пустой, то присваиваем ему имя, сохраняем в amazon S3 и в базе данных
        //в базу передаем отправителя, получателя, описание сообщения, имя файла и дату отправки
        if(!file.isEmpty()){
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            service.uploadFile(file, fileName);

            Users user = userService.checkUser();

            Docs newDoc = new Docs(user.getFullName(), recipient, content, fileName, new Date());
            docsRepository.save(newDoc);
            return "redirect:/sent";
        }
        //если файл пустой, то отправляем пользователю сообщение на страницу
        else {
            redirectAttributes.addFlashAttribute("message",
                    "Файл не выбран! Пожалуйста, загрузите файл-сообщение!");
            return "redirect:/sendMessage";
        }
    }

    //страница "входящие"
    @GetMapping("/inbox")
    public String inbox(Model model) {

        //находим пользователя
        Users user = userService.checkUser();

        //находим все документы и добавляем в arraylist
        Iterable<Docs> all = docsRepository.findAll();
        ArrayList<Docs> ar = new ArrayList<>();
        ArrayList<Docs> resul = new ArrayList<>();
        all.forEach(ar::add);

        //вносим в отдельный arraylist все документы, в коорых имя пользователя совпадает с именем получателя
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

        //находим все документы и добавляем в arraylist
        Iterable<Docs> all = docsRepository.findAll();
        ArrayList<Docs> ar = new ArrayList<>();
        ArrayList<Docs> resul = new ArrayList<>();
        all.forEach(ar::add);

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
    public String changeParam(@RequestParam String name, @RequestParam String patronymic, @RequestParam String surname, @RequestParam String login, @RequestParam String password, @RequestParam String email, Model model){

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
}