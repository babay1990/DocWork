package com.shpaginWork.docWork.models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;

@Entity
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @ManyToOne(cascade = CascadeType.ALL)
    private Users sender;

    @ManyToOne(cascade = CascadeType.ALL)
    private Users executor;

    private String assingment;
    private String assignmentSubject;
    private ArrayList<String> link;
    private Date date;
    private String comment;
    private boolean status;
    private Date dateOfCompletion;

    public Assignment() {};

    public Assignment(Users sender, Users executor, String assingment, ArrayList<String> link, Date date, String assignmentSubject) {
        this.sender = sender;
        this.executor = executor;
        this.assingment = assingment;
        this.link = link;
        this.date = date;
        this.assignmentSubject = assignmentSubject;
    }

    public Assignment(Users sender, Users executor, String assingment, Date date, String assignmentSubject) {
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

    public Users getSender() {
        return sender;
    }

    public void setSender(Users sender) {
        this.sender = sender;
    }

    public Users getExecutor() {
        return executor;
    }

    public void setExecutor(Users executor) {
        this.executor = executor;
    }

    public String getAssingment() {
        return assingment;
    }

    public void setAssingment(String assingment) {
        this.assingment = assingment;
    }

    public ArrayList<String> getLink() {
        return link;
    }

    public void setLink(ArrayList<String> link) {
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
