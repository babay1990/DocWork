package com.shpaginWork.docWork.service;

import com.shpaginWork.docWork.models.Inbox;
import com.shpaginWork.docWork.models.Sent;
import com.shpaginWork.docWork.models.Users;
import com.shpaginWork.docWork.repo.InboxRepository;
import com.shpaginWork.docWork.repo.SentRepository;
import com.shpaginWork.docWork.repo.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

@Service
public class MessageService {

    @Autowired
    private InboxRepository inboxRepository;

    @Autowired
    private SentRepository sentRepository;

    @Autowired
    private CustomUserDetailService userService;

    @Autowired
    private StorageService service;

    public ArrayList<Inbox> getInboxList() {

        //находим все входящие сообщения и помещаем в лист
        Iterable<Inbox> all = inboxRepository.findAll();
        ArrayList<Inbox> inboxList = new ArrayList<>();
        all.forEach(inboxList::add);
        return inboxList;
    }

    public ArrayList<Sent> getSentList() {

        //находим все исходящие сообщениея и помещаем в лист
        Iterable<Sent> all = sentRepository.findAll();
        ArrayList<Sent> sentList = new ArrayList<>();
        all.forEach(sentList::add);
        return sentList;
    }

    public ArrayList<Inbox> getInboxListForCurrentUser(){
        Users user = userService.checkUser();

        //лист, в который будут добавлены все непрочитанные сообщения
        ArrayList<Inbox> resul = new ArrayList<>();

        // находим все входящие сообщения и добавляем в лист ar
        ArrayList<Inbox> ar = getInboxList();

        //вносим в resul все документы, в которых имя пользователя совпадает с именем получателя
        //и в которых отметка о прочитанном сообщении false
        for (Inbox inbox : ar) {
            if (inbox.getRecipient().equals(user) && !inbox.isCheckMessage()) {
                resul.add(inbox);
            }
        }

        return resul;
    }

    //метод отправки сообщения
    public String sendMessage(MultipartFile[] files, Users recipient,
                              String content, String messageSubject) {

        ArrayList<String> filesNamesList = new ArrayList<>();

        if(!files[0].isEmpty()){

            for(MultipartFile file : files) {
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                service.uploadFile(file, fileName);
                filesNamesList.add(fileName);
            }

            Users user = userService.checkUser();
            Date date = new Date();
            Inbox inbox = new Inbox(user, recipient, content, filesNamesList, date, false, messageSubject);
            Sent sent = new Sent(user, recipient, content, filesNamesList, date, messageSubject);
            inboxRepository.save(inbox);
            sentRepository.save(sent);
            return "redirect:/sent";
        }

        else {
            Users user = userService.checkUser();
            Date date = new Date();
            Inbox inbox = new Inbox(user, recipient, content, date, false, messageSubject);
            Sent sent = new Sent(user, recipient, content, date, messageSubject);

            inboxRepository.save(inbox);
            sentRepository.save(sent);

            return "redirect:/sent";
        }
    }

    //метод сортировки входящих сообщений
    public ArrayList<Inbox> sortInboxList (ArrayList<Inbox> list) {
        Collections.sort(list, new Comparator<Inbox>() {
            @Override
            public int compare(Inbox notes, Inbox t1) {
                if (notes.getId() == t1.getId()) return 0;
                else if (notes.getId() < t1.getId()) return 1;
                else return -1;
            }
        });
        return list;
    }

    //метод сортировки входящих сообщений
    public ArrayList<Sent> sortSentList (ArrayList<Sent> list) {
        Collections.sort(list, new Comparator<Sent>() {
            @Override
            public int compare(Sent notes, Sent t1) {
                if (notes.getId() == t1.getId()) return 0;
                else if (notes.getId() < t1.getId()) return 1;
                else return -1;
            }
        });
        return list;
    }


}
