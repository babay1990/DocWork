package com.shpaginWork.docWork.models;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class Users {

    public Users() {};

    public Users(String name, String patronymic, String surname, String login, String password, String role, String email, String fullName) {
        this.login = login;
        this.password = password;
        this.role = role;
        this.email = email;
        this.name = name;
        this.patronymic = patronymic;
        this.surname = surname;
        this.fullName = fullName;

    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;
    private String patronymic;
    private String surname;
    private String fullName;
    private String login;
    private String password;
    private String role;
    private String email;


    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}