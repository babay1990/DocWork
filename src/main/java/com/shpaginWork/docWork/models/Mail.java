package com.shpaginWork.docWork.models;

import javax.persistence.*;
import java.util.HashMap;

@Entity
@Table(name = "mail")
public class Mail {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    private String autor;
    private String signer;
    private String recipient;
    private String organization;
    private String comment;
    private HashMap<String, Boolean> map;
    private String link;

    private boolean registration;
    private boolean sent;

    public Mail(){}

    public Mail(String autor, String signer, String recipient,
                String organization, String comment, HashMap<String,
                Boolean> map, String link, boolean registration, boolean sent) {
        this.autor = autor;
        this.signer = signer;
        this.recipient = recipient;
        this.organization = organization;
        this.comment = comment;
        this.map = map;
        this.link = link;
        this.registration = registration;
        this.sent = sent;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
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

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public boolean isRegistration() {
        return registration;
    }

    public void setRegistration(boolean registration) {
        this.registration = registration;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }
}
