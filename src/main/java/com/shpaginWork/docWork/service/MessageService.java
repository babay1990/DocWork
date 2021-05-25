package com.shpaginWork.docWork.service;

import com.shpaginWork.docWork.models.Inbox;
import com.shpaginWork.docWork.models.Sent;
import com.shpaginWork.docWork.repo.InboxRepository;
import com.shpaginWork.docWork.repo.SentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class MessageService {

    @Autowired
    private InboxRepository inboxRepository;

    @Autowired
    private SentRepository sentRepository;

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

}
