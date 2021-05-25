package com.shpaginWork.docWork.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String sender;
    private String executor;
    private String assingment;
    private String assignmentSubject;
    private String link;
    private Date date;
    private String comment;
    private boolean status;
    private Date dateOfCompletion;

    public Assignment() {};

    public Assignment(String sender, String executor, String assingment, String link, Date date, String assignmentSubject) {
        this.sender = sender;
        this.executor = executor;
        this.assingment = assingment;
        this.link = link;
        this.date = date;
        this.assignmentSubject = assignmentSubject;
    }

    public Assignment(String sender, String executor, String assingment, Date date, String assignmentSubject) {
        this.sender = sender;
        this.executor = executor;
        this.assingment = assingment;
        this.date = date;
        this.assignmentSubject = assignmentSubject;
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

    public String getExecutor() {
        return executor;
    }

    public void setExecutor(String executor) {
        this.executor = executor;
    }

    public String getAssingment() {
        return assingment;
    }

    public void setAssingment(String assingment) {
        this.assingment = assingment;
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

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDateOfCompletion() {
        return dateOfCompletion;
    }

    public void setDateOfCompletion(Date dateOfCompletion) {
        this.dateOfCompletion = dateOfCompletion;
    }

    public String getAssignmentSubject() {
        return assignmentSubject;
    }

    public void setAssignmentSubject(String assignmentSubject) {
        this.assignmentSubject = assignmentSubject;
    }
}
