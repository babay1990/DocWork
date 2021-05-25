package com.shpaginWork.docWork.service;

import com.shpaginWork.docWork.models.Assignment;
import com.shpaginWork.docWork.repo.AssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class AssignmentService {

    @Autowired
    private AssignmentRepository assignmentRepository;

    public ArrayList<Assignment> getList(){

        //находим все поручения и добавляем в arraylist
        Iterable<Assignment> all = assignmentRepository.findAll();
        ArrayList<Assignment> ar = new ArrayList<>();
        all.forEach(ar::add);

        return ar;
    }



}
