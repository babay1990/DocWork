package com.shpaginWork.docWork.service;

import com.shpaginWork.docWork.models.Users;
import com.shpaginWork.docWork.repo.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;


    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Users myUser= usersRepository.findByLogin(userName);
        if (myUser == null) {
            throw new UsernameNotFoundException("Unknown user: "+userName);
        }
        UserDetails user = User.builder()
                .username(myUser.getLogin())
                .password(bCryptPasswordEncoder.encode(myUser.getPassword()))
                .roles(myUser.getRole())
                .build();

        return user;
    }



    //Находим информацию об авторизованном пользователе и передаем в объект user
    public Users checkUser(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Users user = usersRepository.findByLogin(userDetails.getUsername());
        return user;
    }

    //метод реализации поиска по имени
    public ArrayList<Users> searchByUserFullName(String value){

        //лист с результатами поиска
        ArrayList<Users> userNamesList = new ArrayList<>();
        //находим список пользователей
        Iterable<Users> users = usersRepository.findAll();

        //заполняем лист с результатами юзерами, у которых есть совпадение со значением поиска
        //учитываются также варианты ввода в верхнем и нижнем регистрах
        for(Users user : users){
            if(user.getFullName().contains(value) || user.getFullName().toLowerCase().contains(value) ||
            user.getFullName().toUpperCase().contains(value))
            userNamesList.add(user);
        }
        return userNamesList;
    }
}
