package com.shpaginWork.docWork.models;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "sent")
public class Sent {

    public Sent(){}

    public Sent(String sender, String recipient, String content, String link, Date date, String messageSubject){
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
        this.link = link;
        this.date = date;
        this.messageSubject = messageSubject;
    }

    public Sent(String sender, String recipient, String content, Date date, String messageSubject){
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
        this.date = date;
        this.messageSubject = messageSubject;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    private String sender, recipient, content, link, messageSubject;
    private Date date;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getMessageSubject() {
        return messageSubject;
    }

    public void setMessageSubject(String messageSubject) {
        this.messageSubject = messageSubject;
    }
}
