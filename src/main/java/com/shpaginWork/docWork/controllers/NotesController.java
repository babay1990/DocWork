package com.shpaginWork.docWork.controllers;

import com.shpaginWork.docWork.service.CustomUserDetailService;
import com.shpaginWork.docWork.models.Notes;
import com.shpaginWork.docWork.models.Users;
import com.shpaginWork.docWork.repo.NotesRepository;
import com.shpaginWork.docWork.repo.UsersRepository;
import com.shpaginWork.docWork.service.NotesService;
import com.shpaginWork.docWork.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

@Controller
public class NotesController {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private CustomUserDetailService userService;

    @Autowired
    private StorageService service;

    @Autowired
    private NotesRepository notesRepository;

    @Autowired
    private NotesService notesService;

    //страница создания служебной зписки
    @GetMapping("/createSZ")
    public String createSZ(Model model) {

        //передаем на страницу всех пользователей для поиска получателя
        Iterable<Users> block = usersRepository.findAll();
        model.addAttribute("block", block);
        return "createSZ";
    }

    @PostMapping("/createSZ")
    public String newSZ (@RequestParam String recipient,
                         @RequestParam String signer, @RequestParam("file") MultipartFile file,
                         @RequestParam String comment, @RequestParam String name1,
                         @RequestParam String name2, @RequestParam String name3,
                         @RequestParam String name4, @RequestParam String name5) {

        Users user = userService.checkUser();

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        service.uploadFile(file, fileName);

        HashMap<String, Boolean> map = new HashMap<>();

        if(!name1.isEmpty()) {
            map.put(name1, false);
        }

        if(!name2.isEmpty()) {
            map.put(name2, false);
        }

        if(!name3.isEmpty()) {
            map.put(name3, false);
        }

        if(!name4.isEmpty()) {
            map.put(name4, false);
        }

        if(!name5.isEmpty()) {
            map.put(name5, false);
        }

        Notes notes = new Notes(user.getFullName(), signer, recipient, fileName, comment, map, false);
        notesRepository.save(notes);

        return "redirect:/lk";
    }

    // метод архив СЗ
    @GetMapping("/myNotes")
    public String myNotes (Model model) {

        // находим пользователя
        Users user = userService.checkUser();

        // лист list будет передан на страницу как список СЗ, в которых присутствует пользователь
        ArrayList<Notes> list = new ArrayList<>();

        // находим все CЗ и добавляем во временный лист ar
        ArrayList<Notes> ar = notesService.getList();

        // цикл
       for(int i = 0; i < ar.size(); i++) {
           // заполняем лист checks значениями согласовано/не согласовано конкретной СЗ
           ArrayList<Boolean> checks = new ArrayList<>(ar.get(i).getMap().values());

           // заполняем лист allNames именами согласующих СЗ
           Set<String> names = ar.get(i).getMap().keySet();
           ArrayList<String> allNames = new ArrayList<>();
           for(String key : names) {
               allNames.add(key);
           }

           if(user.getFullName().equals(ar.get(i).getSender()) || allNames.contains(user.getFullName())) {
               list.add(ar.get(i));
           }

           if(user.getFullName().equals(ar.get(i).getSigner()) && !checks.contains(false)) {
               list.add(ar.get(i));
           }

           if(user.getFullName().equals(ar.get(i).getRecipient()) && !checks.contains(false) && ar.get(i).isCheck()) {
               list.add(ar.get(i));
           }
       }

        // отправляем список СЗ, в которых участвует пользователь, на страницу
        model.addAttribute("list", list);

        return "myNotes";
    }

    //метод просмотра данных СЗ
    @GetMapping("/notes/{id}")
    public String notesDetails(@PathVariable(value = "id") Long id, Model model){

        String singerCheckTrue = "Утверждено";
        String singerCheckFalse = "Не утверждено";
        Set<String> nam;
        ArrayList<String> names = new ArrayList<>();
        ArrayList<Boolean> checks;

        // находим пользователя
        Users user = userService.checkUser();

        //по переданному id находим СЗ
        Optional<Notes> op = notesRepository.findById(id);
        Notes note = op.get();


        // заполняем лист checks значениями согласовано/не согласовано
        checks = new ArrayList<>(note.getMap().values());
        ArrayList<String> checkList = new ArrayList<>();
        for(int y = 0; y < checks.size(); y++) {
            if(!checks.get(y)) checkList.add("Не согласовано");
            if(checks.get(y)) checkList.add("Согласовано");
        }

        // заполняем сет значениями имен согласующих
        nam = note.getMap().keySet();

        // заполняем лист names значениями имен согласующих
        for(String key : nam){
            names.add(key);
        }

        // если имя пользователя совпадает с именами отправителя или адресата СЗ, то передаем все значения
        if(user.getFullName().equals(note.getSender()) || user.getFullName().equals(note.getRecipient())) {
            model.addAttribute("note", note);
            model.addAttribute("names", names);
            model.addAttribute("checks", checkList);
            if(note.isCheck()) model.addAttribute("singerCheck", singerCheckTrue);
            if(!note.isCheck()) model.addAttribute("singerCheck", singerCheckFalse);

        }

        // если в списке согласующих есть пользователь, то передаем все значения и возможность согласовать СЗ
        if(names.contains(user.getFullName())) {
            model.addAttribute("note", note);
            model.addAttribute("names", names);
            model.addAttribute("checks", checkList);
            model.addAttribute("checker", note.getId());
            if(note.isCheck()) model.addAttribute("singerCheck", singerCheckTrue);
            if(!note.isCheck()) model.addAttribute("singerCheck", singerCheckFalse);
        }

        // если имя пользователя совпадаетс именем подписанта,то передаем все значения
        if(user.getFullName().equals(note.getSigner())) {
            model.addAttribute("note", note);
            model.addAttribute("names", names);
            model.addAttribute("checks", checkList);
            if(note.isCheck()) model.addAttribute("singerCheck", singerCheckTrue);
            if(!note.isCheck()) model.addAttribute("singerCheck", singerCheckFalse);

            // если в листе согласовано/не согласовано все значения "согласовано" и
            // значения check CЗ false, то передаем возможность утвердить СЗ
            if(!checks.contains(false) && !note.isCheck()) {
                model.addAttribute("signerChecker", note.getId());
            }
        }
        return "notesDetails";
    }

    //метод согласования служебной записки
    @PostMapping(value = "/notes/{id}", params = "checkId")
    public String checkName(@RequestParam Long newCheck, Model model) {

        //находим пользователя
        Users user = userService.checkUser();

        //по переданному из формы id находим СЗ
        Optional<Notes> noteOp = notesRepository.findById(newCheck);
        Notes note = noteOp.get();

        // меняем в поле map значение на true (согласовано) по ключу-имени текущего пользователя
        // сохраняем СЗ
        note.getMap().put(user.getFullName(), true);
        notesRepository.save(note);

        return "redirect:/myNotes";
    }

    //метод подписания служебной записки
    @PostMapping(value = "/notes/{id}", params = "checkSignerId")
    public String checkSigner(@RequestParam Long newSignerCheck, Model model) {

        //находим пользователя
        Users user = userService.checkUser();

        //по переданному из формы id находим СЗ
        Optional<Notes> noteOp = notesRepository.findById(newSignerCheck);
        Notes note = noteOp.get();

        //меняем поле check (утверждено) в СЗ на true и сохраняем СЗ
        note.setCheck(true);
        notesRepository.save(note);

        return "redirect:/myNotes";
    }

    @GetMapping("/noteForApproval")
    public String getNoteForApproval(Model model) {

        Users user = userService.checkUser();

        // лист для передачи на страницу, содержащий СЗ для согласования с пользователем
        ArrayList<Notes> list = new ArrayList<>();

        // находим все CЗ и добавляем во временный лист ar
        ArrayList<Notes> ar = notesService.getList();

        //цикл, в котором перебираем лист с СЗ и отбираем те, где пользователь в роли согласующего
        //и которые еще не согласовал и заполняем ими лист
        for(int i = 0; i < ar.size(); i++){
            // заполняем лист checks значениями согласовано/не согласовано конкретной СЗ
            ArrayList<Boolean> checks = new ArrayList<>(ar.get(i).getMap().values());

            // заполняем лист allNames именами согласующих СЗ
            Set<String> names = ar.get(i).getMap().keySet();
            ArrayList<String> allNames = new ArrayList<>();
            for(String key : names) {
                allNames.add(key);
            }

            //перебираем листы с именами и чеками на совпадение с именем пользователя и, если имя совпадает и
            //значения чека false, добавляем СЗ в лист
            for(int j = 0; j < allNames.size(); j++){
                if(user.getFullName().equals(allNames.get(j)) && !checks.get(j)){
                    list.add(ar.get(i));
                }
            }
        }
        model.addAttribute("list", list);
        return "noteForApproval";
    }

    @GetMapping("/lastApprove")
    public String getLastApprove(Model model){

        Users user = userService.checkUser();

        // лист для передачи на страницу, содержащий СЗ для согласования с пользователем
        ArrayList<Notes> list = new ArrayList<>();

        // находим все CЗ и добавляем во временный лист ar
        ArrayList<Notes> ar = notesService.getList();


        for(int i = 0; i < ar.size(); i ++){

            // заполняем лист checks значениями согласовано/не согласовано конкретной СЗ
            ArrayList<Boolean> checks = new ArrayList<>(ar.get(i).getMap().values());

            if(user.getFullName().equals(ar.get(i).getSigner()) && !checks.contains(false) && !ar.get(i).isCheck()) {
                list.add(ar.get(i));
            }
        }

        model.addAttribute("list", list);
        return "lastApprove";
    }

    @GetMapping("/notesToMe")
    public String getNotesToMe(Model model){

        Users user = userService.checkUser();

        // лист для передачи на страницу, содержащий СЗ для согласования с пользователем
        ArrayList<Notes> list = new ArrayList<>();

        // находим все CЗ и добавляем во временный лист ar
        ArrayList<Notes> ar = notesService.getList();

        for(int i = 0; i < ar.size(); i++) {

            // заполняем лист checks значениями согласовано/не согласовано конкретной СЗ
            ArrayList<Boolean> checks = new ArrayList<>(ar.get(i).getMap().values());

            if (user.getFullName().equals(ar.get(i).getRecipient()) && !checks.contains(false) && ar.get(i).isCheck()) {
                list.add(ar.get(i));
            }
        }
        model.addAttribute("list", list);

        return "notesToMe";
    }
}
