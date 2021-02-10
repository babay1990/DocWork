package com.shpaginWork.docWork.repo;

import com.shpaginWork.docWork.models.Sent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SentRepository extends JpaRepository<Sent, Long> {
    Sent findBySender(String sender);
    Sent findByRecipient(String recipient);
    Sent save(Sent sent);
    Sent findByContent(String content);
}
