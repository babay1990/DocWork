package com.shpaginWork.docWork.models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "news")
public class News {

    public News(){}

    public News(String title, String annotation, String text, String autor_login, String autor_name){
        this.title = title;
        this.annotation = annotation;
        this.text = text;
        this.autor_login = autor_login;
        this.autor_name = autor_name;
    }

    @Id
    private long id;

    private String title, annotation, text, autor_login, autor_name;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAutor_login() {
        return autor_login;
    }

    public void setAutor_login(String autor_login) {
        this.autor_login = autor_login;
    }

    public String getAutor_name() {
        return autor_name;
    }

    public void setAutor_name(String autor_name) {
        this.autor_name = autor_name;
    }
}
