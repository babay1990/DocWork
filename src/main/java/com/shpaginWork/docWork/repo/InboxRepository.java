package com.shpaginWork.docWork.repo;

import com.shpaginWork.docWork.models.Inbox;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InboxRepository extends JpaRepository<Inbox, Long> {
    Inbox findBySender(String sender);
    Inbox findByRecipient(String recipient);
    Inbox save(Inbox inbox);
    Inbox findByContent(String content);
}
