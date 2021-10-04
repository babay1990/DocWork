package com.shpaginWork.docWork.service;

import com.shpaginWork.docWork.models.Mail;
import com.shpaginWork.docWork.models.Users;
import com.shpaginWork.docWork.repo.MailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class MailService {

    @Autowired
    private CustomUserDetailService userService;

    @Autowired
    private StorageService service;

    @Autowired
    private MailRepository mailRepository;

    public String createMail (String signer, String recipient,
                              String organization, String comment,
                              String name1, MultipartFile file,
                              String name2, String name3,
                              String name4, String name5) {

        if(userService.checkUserByFullName(signer)) {

            //Находим текущего юзера
            Users user = userService.checkUser();
            HashMap<String, Boolean> map = new HashMap<>();


            if(!name1.isEmpty()) {
                if(userService.checkUserByFullName(name1))
                    map.put(name1, false);
                else return "Неверно введен согласующий. Попробуйте еще раз.";
            }

            if(!name2.isEmpty()) {
                if(userService.checkUserByFullName(name2))
                    map.put(name2, false);
                else return "Неверно введен согласующий. Попробуйте еще раз.";
            }

            if(!name3.isEmpty()) {
                if(userService.checkUserByFullName(name3))
                    map.put(name3, false);
                else return "Неверно введен согласующий. Попробуйте еще раз.";
            }

            if(!name4.isEmpty()) {
                if(userService.checkUserByFullName(name4))
                    map.put(name4, false);
                else return "Неверно введен согласующий. Попробуйте еще раз.";
            }

            if(!name5.isEmpty()) {
                if(userService.checkUserByFullName(name5))
                    map.put(name5, false);
                else return "Неверно введен согласующий. Попробуйте еще раз.";
            }

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            service.uploadFile(file, fileName);

            Mail mail = new Mail(user.getFullName(), signer, recipient, organization, comment, map, fileName, false, false);
            mailRepository.save(mail);

            return "Письмо отправлено в канцелярию.";
        }
        else return "Неверно введен подписант. Попробуйте еще раз.";
    }

    //метод получения листа со всеми исходящими сообщениями
    public ArrayList<Mail> getList(){

        Iterable<Mail> all = mailRepository.findAll();
        ArrayList<Mail> ar = new ArrayList<>();
        all.forEach(ar::add);
        return ar;
    }


    //метод сортировки
    public ArrayList<Mail> sortList (ArrayList<Mail> list) {
        Collections.sort(list, new Comparator<Mail>() {
            @Override
            public int compare(Mail notes, Mail t1) {
                if (notes.getId() == t1.getId()) return 0;
                else if (notes.getId() < t1.getId()) return 1;
                else return -1;
            }
        });
        return list;
    }


    //архив служебных записок пользователя
    //метод передачи исходящих писем, в которых участвовал пользователь
    public ArrayList<Mail> getNotesListArchive(){

        // находим пользователя
        Users user = userService.checkUser();

        // лист list будет передан на страницу как список писем, в которых присутствует пользователь
        ArrayList<Mail> list = new ArrayList<>();

        // находим все письма и добавляем во временный лист ar
        ArrayList<Mail> ar = getList();

        // цикл
        for(int i = 0; i < ar.size(); i++) {
            // заполняем лист checks значениями согласовано/не согласовано конкретной СЗ
            ArrayList<Boolean> checks = new ArrayList<>(ar.get(i).getMap().values());

            // заполняем лист allNames именами согласующих СЗ
            Set<String> names = ar.get(i).getMap().keySet();
            ArrayList<String> allNames = new ArrayList<>();
            for(String key : names) {
                allNames.add(key);
            }

            if(user.getFullName().equals(ar.get(i).getAutor()) || allNames.contains(user.getFullName())) {
                list.add(ar.get(i));
            }

            if(user.getFullName().equals(ar.get(i).getSigner()) && !checks.contains(false)) {
                list.add(ar.get(i));
            }
        }

        sortList(list);
        return list;
    }


}
