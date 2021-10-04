package com.shpaginWork.docWork.repo;

import com.shpaginWork.docWork.models.Mail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MailRepository extends JpaRepository<Mail, Long> {
}
