package com.shpaginWork.docWork.service;

import com.shpaginWork.docWork.models.Notes;
import com.shpaginWork.docWork.models.Users;
import com.shpaginWork.docWork.repo.NotesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class NotesService {

    @Autowired
    private NotesRepository notesRepository;

    @Autowired
    private CustomUserDetailService userService;

    @Autowired
    private StorageService service;

    //метод получения листа со всеми СЗ
    public ArrayList<Notes> getList(){

        Iterable<Notes> all = notesRepository.findAll();
        ArrayList<Notes> ar = new ArrayList<>();
        all.forEach(ar::add);

        return ar;
    }

    //метод получения списка согласующих служебной записки
    public ArrayList<String> getNamesList(Notes note){

        Set<String> names = note.getMap().keySet();
        ArrayList<String> allNames = new ArrayList<>();
        for(String key : names) {
            allNames.add(key);
        }

        return allNames;
    }

    //метод получения количества СЗ для согласования юзером
    public int getNotesNamesListForCurrentUser(){
        Users user = userService.checkUser();
        int notesNamesCount = 0;

        //получаем список всех служебных записок
        ArrayList<Notes> arNotes = getList();

        for(int i = 0; i < arNotes.size(); i++) {

            // заполняем лист checks значениями согласовано/не согласовано конкретной СЗ
            ArrayList<Boolean> checks = new ArrayList<>(arNotes.get(i).getMap().values());

            // заполняем лист allNames именами согласующих СЗ
            ArrayList<String> allNames = getNamesList(arNotes.get(i));

            // теперь есть лист с чеками checks и лист с именами согласующих allNames
            // если имя согласующего совпадает с именем юзера и отметка согласующего false
            // то увеличиваем счетчик "на согласование"
            for(int j = 0; j < allNames.size(); j++) {
                if(allNames.get(j).equals(user.getFullName()) && !checks.get(j)) {
                    notesNamesCount++;
                }
            }

        }

        //возвращаем количество служебных записок, где требуется согласование текущего user
        return notesNamesCount;
    }

    //метод получения количества СЗ для утверждения юзером
    public int getNotesSignerListForCurrentUser(){

        Users user = userService.checkUser();
        int notesSignerCount = 0;

        //получаем список всех служебных записок
        ArrayList<Notes> arNotes = getList();

        for(int i = 0; i < arNotes.size(); i++) {

            // заполняем лист checks значениями согласовано/не согласовано конкретной СЗ
            ArrayList<Boolean> checks = new ArrayList<>(arNotes.get(i).getMap().values());

            // если имя подписанта совпадает с именем юзера, все отметки согласования не содержат false
            // и отметка утверждения документа false, то увеличиваем счетчик "на утверждение"
            if(arNotes.get(i).getSigner().equals(user.getFullName()) && !arNotes.get(i).isCheck() && !checks.contains(false)) {
                notesSignerCount++;
            }
        }

        //возвращаем количество СЗ на утверждение
        return notesSignerCount;
    }

    //метод создания служебной записки
    public String createNote(String recipient,
                             String signer, MultipartFile file,
                             String comment, String name1,
                             String name2, String name3,
                             String name4, String name5) {

        if(userService.checkUserByFullName(recipient) && userService.checkUserByFullName(signer)) {

            //Находим текущего юзера
            Users user = userService.checkUser();
            HashMap<String, Boolean> map = new HashMap<>();


            if(!name1.isEmpty()) {
                if(userService.checkUserByFullName(name1))
                    map.put(name1, false);
                else return "Неверно введен согласующий. Попробуйте еще раз.";
            }

            if(!name2.isEmpty()) {
                if(userService.checkUserByFullName(name2))
                    map.put(name2, false);
                else return "Неверно введен согласующий. Попробуйте еще раз.";
            }

            if(!name3.isEmpty()) {
                if(userService.checkUserByFullName(name3))
                    map.put(name3, false);
                else return "Неверно введен согласующий. Попробуйте еще раз.";
            }

            if(!name4.isEmpty()) {
                if(userService.checkUserByFullName(name4))
                    map.put(name4, false);
                else return "Неверно введен согласующий. Попробуйте еще раз.";
            }

            if(!name5.isEmpty()) {
                if(userService.checkUserByFullName(name5))
                    map.put(name5, false);
                else return "Неверно введен согласующий. Попробуйте еще раз.";
            }

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            service.uploadFile(file, fileName);

            Notes notes = new Notes(user.getFullName(), signer, recipient, fileName, comment, map, false);
            notesRepository.save(notes);

            return "Служебная записка отправлена.";
        }
        else return "Неверно введен подписант или получатель. Попробуйте еще раз.";
    }


    //архив служебных записок пользователя
    //метод передачи служебных записок, в которых участвовал пользователь
    public ArrayList<Notes> getNotesListArchive(){

        // находим пользователя
        Users user = userService.checkUser();

        // лист list будет передан на страницу как список СЗ, в которых присутствует пользователь
        ArrayList<Notes> list = new ArrayList<>();

        // находим все CЗ и добавляем во временный лист ar
        ArrayList<Notes> ar = getList();

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

        sortList(list);
        return list;
    }

    //метод сортировки
    public ArrayList<Notes> sortList (ArrayList<Notes> list) {
        Collections.sort(list, new Comparator<Notes>() {
            @Override
            public int compare(Notes notes, Notes t1) {
                if (notes.getId() == t1.getId()) return 0;
                else if (notes.getId() < t1.getId()) return 1;
                else return -1;
            }
        });
        return list;
    }
}
