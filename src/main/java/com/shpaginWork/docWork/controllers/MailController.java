package com.shpaginWork.docWork.controllers;

import com.shpaginWork.docWork.models.Mail;
import com.shpaginWork.docWork.models.Users;
import com.shpaginWork.docWork.repo.MailRepository;
import com.shpaginWork.docWork.repo.UsersRepository;
import com.shpaginWork.docWork.service.CustomUserDetailService;
import com.shpaginWork.docWork.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

@Controller
public class MailController {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private MailRepository mailRepository;

    @Autowired
    private CustomUserDetailService userService;

    @Autowired
    private MailService mailService;

    @Autowired
    private JavaMailSender emailSender;

    //страница создания исходящего письма
    @GetMapping("/createMail")
    public String createMail(Model model){
        if(userService.isAdmin()){
            model.addAttribute("isAdmin", "Панель администратора");
        }
        if(userService.isSecretary()) model.addAttribute("isSecretary", "Панель канцелярии");

        //передаем на страницу всех пользователей для поиска получателя
        model.addAttribute("block", usersRepository.findAll());

        return "createMail";
    }

    //отправка исходящего письма
    @PostMapping("/createMail")
    public String postCreateMail(@RequestParam String signer, @RequestParam String recipient,
                                 @RequestParam String organization, @RequestParam String comment,
                                 @RequestParam String name1, RedirectAttributes redirectAttributes,
                                 @RequestParam String name2, @RequestParam String name3,
                                 @RequestParam String name4, @RequestParam String name5, @RequestParam("file") MultipartFile file){

        String message = mailService.createMail(signer, recipient, organization, comment, name1, file, name2, name3, name4, name5);

        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/createMail";
    }


    //страница "Мне на согласование"
    @GetMapping("/mailForApproval")
    public String getMailForApproval(Model model) {
        if(userService.isAdmin()){
            model.addAttribute("isAdmin", "Панель администратора");
        }
        if(userService.isSecretary()) model.addAttribute("isSecretary", "Панель канцелярии");

        Users user = userService.checkUser();

        // лист для передачи на страницу, содержащий почту для согласования с пользователем
        ArrayList<Mail> list = new ArrayList<>();

        // находим все CЗ и добавляем во временный лист ar
        ArrayList<Mail> ar = mailService.getList();

        //цикл, в котором перебираем лист с почтой и отбираем те, где пользователь в роли согласующего
        //и которые еще не согласовал и заполняем ими лист
        for(int i = 0; i < ar.size(); i++){
            // заполняем лист checks значениями согласовано/не согласовано конкретного исходящего сообщения
            ArrayList<Boolean> checks = new ArrayList<>(ar.get(i).getMap().values());

            // заполняем лист allNames именами согласующих СЗ
            Set<String> names = ar.get(i).getMap().keySet();
            ArrayList<String> allNames = new ArrayList<>();
            for(String key : names) {
                allNames.add(key);
            }

            //перебираем листы с именами и чеками на совпадение с именем пользователя и, если имя совпадает и
            //значения чека false, добавляем исходящее сообщение в лист
            for(int j = 0; j < allNames.size(); j++){
                if(user.getFullName().equals(allNames.get(j)) && !checks.get(j)){
                    list.add(ar.get(i));
                }
            }
        }

        mailService.sortList(list);
        model.addAttribute("list", list);
        return "mailForApproval";
    }


    //страница "Мне на утверждение"
    @GetMapping("/lastMailApprove")
    public String getLastMailApprove(Model model){
        if(userService.isAdmin()){
            model.addAttribute("isAdmin", "Панель администратора");
        }
        if(userService.isSecretary()) model.addAttribute("isSecretary", "Панель канцелярии");

        Users user = userService.checkUser();

        // лист для передачи на страницу, содержащий исходящие для согласования с пользователем
        ArrayList<Mail> list = new ArrayList<>();

        // находим все CЗ и добавляем во временный лист ar
        ArrayList<Mail> ar = mailService.getList();

        for(int i = 0; i < ar.size(); i ++){
            // заполняем лист checks значениями согласовано/не согласовано конкретной СЗ
            ArrayList<Boolean> checks = new ArrayList<>(ar.get(i).getMap().values());

            if(user.getFullName().equals(ar.get(i).getSigner()) && !checks.contains(false) && !ar.get(i).isRegistration()) {
                list.add(ar.get(i));
            }
        }
        mailService.sortList(list);
        model.addAttribute("list", list);
        return "lastMailApprove";
    }

    //метод просмотра данных исходящих писем
    @GetMapping("/mail/{id}")
    public String mailDetails(@PathVariable(value = "id") Long id, Model model, RedirectAttributes redirectAttributes){
        if(userService.isAdmin()){
            model.addAttribute("isAdmin", "Панель администратора");
        }
        if(userService.isSecretary()) model.addAttribute("isSecretary", "Панель канцелярии");

        Mail mail;
        String singerCheckTrue = "Подписано";
        String singerCheckFalse = "Не подписано";
        Set<String> nam;
        ArrayList<String> names = new ArrayList<>();
        ArrayList<Boolean> checks;

        // находим пользователя
        Users user = userService.checkUser();

        //по переданному id находим исходящее письмо
        Optional<Mail> op = mailRepository.findById(id);

        //если исходящее письмо не находится по значению id, то передаем сообщение пользователю
        if(op.isPresent()){
            mail = op.get();
        }
        else {
            redirectAttributes.addFlashAttribute("message",
                    "Произошла ошибка. Обратитесь в службу поддержки.");
            return "redirect:/myNotes";
        }


        // заполняем лист checks значениями согласовано/не согласовано
        checks = new ArrayList<>(mail.getMap().values());
        ArrayList<String> checkList = new ArrayList<>();
        for(int y = 0; y < checks.size(); y++) {
            if(!checks.get(y)) checkList.add("Не согласовано");
            if(checks.get(y)) checkList.add("Согласовано");
        }

        // заполняем сет значениями имен согласующих
        nam = mail.getMap().keySet();

        // заполняем лист names значениями имен согласующих
        for(String key : nam){
            names.add(key);
        }

        // если имя пользователя совпадает с именами автора, то передаем все значения
        if(user.getFullName().equals(mail.getAutor())) {
            model.addAttribute("note", mail);
            model.addAttribute("names", names);
            model.addAttribute("checks", checkList);
            if(mail.isRegistration()) model.addAttribute("singerCheck", singerCheckTrue);
            if(!mail.isRegistration()) model.addAttribute("singerCheck", singerCheckFalse);

        }

        // если в списке согласующих есть пользователь, то передаем все значения и возможность согласовать СЗ
        if(names.contains(user.getFullName())) {
            model.addAttribute("note", mail);
            model.addAttribute("names", names);
            model.addAttribute("checks", checkList);

            for(int i = 0; i < names.size(); i++){
                if(names.get(i).equals(user.getFullName()) && checkList.get(i).equals("Не согласовано"))
                    model.addAttribute("checker", mail.getId());
            }
            if(mail.isRegistration()) model.addAttribute("singerCheck", singerCheckTrue);
            if(!mail.isRegistration()) model.addAttribute("singerCheck", singerCheckFalse);
        }

        // если имя пользователя совпадает с именем подписанта,то передаем все значения
        if(user.getFullName().equals(mail.getSigner())) {
            model.addAttribute("note", mail);
            model.addAttribute("names", names);
            model.addAttribute("checks", checkList);
            if(mail.isRegistration()) model.addAttribute("singerCheck", singerCheckTrue);
            if(!mail.isRegistration()) model.addAttribute("singerCheck", singerCheckFalse);

            // если в листе согласовано/не согласовано все значения "согласовано" и
            // значения check CЗ false, то передаем возможность утвердить СЗ
            if(!checks.contains(false) && !mail.isRegistration()) {
                model.addAttribute("signerChecker", mail.getId());
            }
        }
        return "mailDetails";
    }


    //метод согласования исходящего письма
    @PostMapping(value = "/mail/{id}", params = "checkId")
    public String checkName(@RequestParam Long newCheck, Model model, RedirectAttributes redirectAttributes) {

        Mail mail;
        //находим пользователя
        Users user = userService.checkUser();

        //по переданному из формы id находим исходящее письмо
        Optional<Mail> noteOp = mailRepository.findById(newCheck);

        //если исходящее письмо не находится по значению id, то передаем сообщение пользователю
        if(noteOp.isPresent()){
            mail = noteOp.get();
        }
        else {
            redirectAttributes.addFlashAttribute("message",
                    "Произошла ошибка. Обратитесь в службу поддержки.");
            return "redirect:/myMail";
        }

        // меняем в поле map значение на true (согласовано) по ключу-имени текущего пользователя
        // сохраняем исходящее письмо
        mail.getMap().put(user.getFullName(), true);
        mailRepository.save(mail);

        return "redirect:/myMail";
    }


    //метод подписания исходящего письма
    @PostMapping(value = "/mail/{id}", params = "checkSignerId")
    public String checkSigner(@RequestParam Long newSignerCheck, Model model, RedirectAttributes redirectAttributes) {

        Mail mail;
        //находим пользователя
        Users user = userService.checkUser();

        //по переданному из формы id находим СЗ
        Optional<Mail> noteOp = mailRepository.findById(newSignerCheck);
        if(noteOp.isPresent()){
            mail = noteOp.get();
        }
        else {
            redirectAttributes.addFlashAttribute("message",
                    "Произошла ошибка. Обратитесь в службу поддержки.");
            return "redirect:/myMail";
        }

        //меняем поле registration (зарегестрировано) в письме на true и сохраняем письмо
        mail.setRegistration(true);
        mailRepository.save(mail);

        return "redirect:/myMail";
    }

    // метод архив исходящих писем
    @GetMapping("/myMail")
    public String myMail (Model model) {
        if(userService.isAdmin()){
            model.addAttribute("isAdmin", "Панель администратора");
        }
        if(userService.isSecretary()) model.addAttribute("isSecretary", "Панель канцелярии");

        // лист list будет передан на страницу как список СЗ, в которых присутствует пользователь
        ArrayList<Mail> list = mailService.getNotesListArchive();
        model.addAttribute("list", list);

        return "myMail";
    }

    @GetMapping("/mailToSent")
    public String mailToSent(Model model){
        if(userService.isAdmin()){
            model.addAttribute("isAdmin", "Панель администратора");
        }
        if(userService.isSecretary()) model.addAttribute("isSecretary", "Панель канцелярии");

        //лист, в котором будут содержаться все сообщения для отправки
        ArrayList<Mail> list = new ArrayList<>();

        // находим все письма и добавляем во временный лист
        ArrayList<Mail> mailList = mailService.getList();
        for(int i = 0; i < mailList.size(); i++){
            if(mailList.get(i).isRegistration() && !mailList.get(i).isSent()) list.add(mailList.get(i));
        }

        model.addAttribute("list", list);

        return "mailToSent";
    }

    @PostMapping(value = "/mailToSent", params = "sent")
    public String sendMailByPost(@RequestParam Long id, Model model, RedirectAttributes redirectAttributes){

        Mail mail;
        //по переданному id находим исходящее письмо
        Optional<Mail> op = mailRepository.findById(id);

        //если исходящее письмо не находится по значению id, то передаем сообщение пользователю
        if(op.isPresent()){
            mail = op.get();
        }
        else {
            redirectAttributes.addFlashAttribute("message",
                    "Произошла ошибка. Обратитесь в службу поддержки.");
            return "redirect:/myNotes";
        }

        mail.setSent(true);
        mailRepository.save(mail);
        return "mailToSent";
    }

    @GetMapping("/sendByEmail/{id}")
    public String sendByEmail(@PathVariable(value = "id") Long id, Model model, RedirectAttributes redirectAttributes){
        if(userService.isAdmin()){
            model.addAttribute("isAdmin", "Панель администратора");
        }
        if(userService.isSecretary()) model.addAttribute("isSecretary", "Панель канцелярии");

        Mail mail;
        //по переданному id находим исходящее письмо
        Optional<Mail> op = mailRepository.findById(id);

        //если исходящее письмо не находится по значению id, то передаем сообщение пользователю
        if(op.isPresent()){
            mail = op.get();
        }
        else {
            redirectAttributes.addFlashAttribute("message",
                    "Произошла ошибка. Обратитесь в службу поддержки.");
            return "redirect:/myNotes";
        }

        model.addAttribute("mail", mail);

        return "sendByEmail";
    }

    @PostMapping(value = "/sendByEmail/{id}", params = "sendEmail")
    public String postSendByEmail(@PathVariable(value = "id") Long id, @RequestParam String email,
                                  @RequestParam("file") MultipartFile file, Model model, RedirectAttributes redirectAttributes)
            throws MessagingException, IOException, IllegalStateException {
        if(userService.isAdmin()){
            model.addAttribute("isAdmin", "Панель администратора");
        }
        if(userService.isSecretary()) model.addAttribute("isSecretary", "Панель канцелярии");

        Mail mail;
        //по переданному id находим исходящее письмо
        Optional<Mail> op = mailRepository.findById(id);

        //если исходящее письмо не находится по значению id, то передаем сообщение пользователю
        if(op.isPresent()){
            mail = op.get();
        }
        else {
            redirectAttributes.addFlashAttribute("message",
                    "Произошла ошибка. Обратитесь в службу поддержки.");
            return "redirect:/myNotes";
        }

        MimeMessage message = emailSender.createMimeMessage();

        boolean multipart = true;

        MimeMessageHelper helper = new MimeMessageHelper(message, multipart);

        helper.setFrom("shpaginjava@gmail.com");
        helper.setTo(email);
        helper.setSubject("Письмо от docWork");

        helper.setText("Добрый день, навправляем Вам Письмо!");


        File convFile = new File(file.getOriginalFilename());
        convFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();


        FileSystemResource file1 = new FileSystemResource(convFile);
        helper.addAttachment(file1.getFilename(), file1);


        emailSender.send(message);

        mail.setSent(true);
        mailRepository.save(mail);

        redirectAttributes.addFlashAttribute("message",
                "Письмо отправлено!");

        return "mailToSent";
    }


}
