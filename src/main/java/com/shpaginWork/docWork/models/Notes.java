package com.shpaginWork.docWork.models;

import javax.persistence.*;
import java.util.HashMap;

@Entity
@Table(name = "notes")
public class Notes {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    private String sender, signer, recipient, link, comment;
    private HashMap<String, Boolean> map;
    private boolean checks;

    public Notes () {}

    public Notes (String sender, String signer, String recipient, String link,
                  String comment, HashMap<String, Boolean> map, boolean check) {
        this.sender = sender;
        this.signer = signer;
        this.recipient = recipient;
        this.link = link;
        this.comment = comment;
        this.map = map;
        this.checks = check;
    }

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

    public String getSigner() {
        return signer;
    }

    public void setSigner(String signer) {
        this.signer = signer;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public HashMap<String, Boolean> getMap() {
        return map;
    }

    public void setMap(HashMap<String, Boolean> map) {
        this.map = map;
    }

    public boolean isCheck() {
        return checks;
    }

    public void setCheck(boolean check) {
        this.checks = check;
    }
}
