package com.shpaginWork.docWork.models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;

@Entity
@Table(name = "inbox")
public class Inbox {

    public Inbox(){}

    public Inbox(Users sender, Users recipient, String content, ArrayList<String> link, Date date, boolean checkMessage, String messageSubject){
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
        this.link = link;
        this.date = date;
        this.checkMessage = checkMessage;
        this.messageSubject = messageSubject;
    }

    public Inbox(Users sender, Users recipient, String content, Date date, boolean checkMessage, String messageSubject){
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
        this.date = date;
        this.checkMessage = checkMessage;
        this.messageSubject = messageSubject;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    private String content, messageSubject;

    @ManyToOne(cascade = CascadeType.ALL)
    private Users sender;

    @ManyToOne(cascade = CascadeType.ALL)
    private Users recipient;

    private ArrayList<String> link;
    private Date date;
    private boolean checkMessage;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Users getSender() {
        return sender;
    }

    public void setSender(Users sender) {
        this.sender = sender;
    }

    public Users getRecipient() {
        return recipient;
    }

    public void setRecipient(Users recipient) {
        this.recipient = recipient;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ArrayList<String> getLink() {
        return link;
    }

    public void setLink(ArrayList<String> link) {
        this.link = link;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isCheckMessage() {
        return checkMessage;
    }

    public void setCheckMessage(boolean checkMessage) {
        this.checkMessage = checkMessage;
    }

    public String getMessageSubject() {
        return messageSubject;
    }

    public void setMessageSubject(String messageSubject) {
        this.messageSubject = messageSubject;
    }
}