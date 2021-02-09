package com.shpaginWork.docWork;

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

    public Users checkUser(){
        //Находим информацию об авторизованном пользователе и передаем в объект user
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Users user = usersRepository.findByLogin(userDetails.getUsername());
        return user;
    }
}
