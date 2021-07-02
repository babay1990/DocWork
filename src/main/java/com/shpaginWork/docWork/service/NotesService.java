package com.shpaginWork.docWork.service;

import com.shpaginWork.docWork.models.Notes;
import com.shpaginWork.docWork.models.Users;
import com.shpaginWork.docWork.repo.NotesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Set;

@Service
public class NotesService {

    @Autowired
    private NotesRepository notesRepository;

    @Autowired
    private CustomUserDetailService userService;

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

}
