package com.shpaginWork.docWork.testing;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HumanController {

    @PostMapping("/human")
    public void getHuman(@RequestBody Human human){

        System.out.println(human.getName());
        System.out.println(human.getSurname());
        System.out.println(human.getAge());
    }

    @GetMapping("/human")
    public ResponseEntity<Human> doHuman(){

        Human human = new Human("Sasha", "Shpagin", "30");

        return new ResponseEntity<>(human, HttpStatus.OK);
    }
}
