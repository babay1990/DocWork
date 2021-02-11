package com.shpaginWork.docWork.repo;

import com.shpaginWork.docWork.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {

    Users findByLogin(String login);
    Users save(Users user);
    Users findByFullName(String fullName);
}
