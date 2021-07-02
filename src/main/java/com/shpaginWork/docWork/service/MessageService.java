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
            if (inbox.getRecipient().equals(user.getFullName()) && !inbox.isCheckMessage()) {
                resul.add(inbox);
            }
        }

        return resul;
    }

    //метод отправки сообщения
    public String sendMessage(MultipartFile file, String recipient,
                              String content, String messageSubject) {

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

}
