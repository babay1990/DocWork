package com.shpaginWork.docWork.models;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class Users {

    public Users() {};

    public Users(String url) {
        this.url = url;
    }

    public Users(String name, String patronymic, String surname, String department, String login, String password, String role, String email) {
        this.login = login;
        this.password = password;
        this.role = role;
        this.email = email;
        this.name = name;
        this.patronymic = patronymic;
        this.surname = surname;
        this.department = department;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;
    private String patronymic;
    private String surname;
    private String department;
    private String login;
    private String password;
    private String role;
    private String email;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private String url;

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

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}