package com.shpaginWork.docWork.service;

import com.shpaginWork.docWork.models.Notes;
import com.shpaginWork.docWork.repo.NotesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Set;

@Service
public class NotesService {

    @Autowired
    private NotesRepository notesRepository;

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

}
