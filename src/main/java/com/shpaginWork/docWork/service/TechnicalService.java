package com.shpaginWork.docWork.service;

import org.springframework.stereotype.Service;

@Service
public class TechnicalService {

    //проверка на наличие цифр в ФИО
    public boolean checkForNumbers (String s) {
        for(char ch : s.toCharArray()){
            if(Character.isDigit(ch)){
                return true;
            }
        }
        return false;
    }

}
