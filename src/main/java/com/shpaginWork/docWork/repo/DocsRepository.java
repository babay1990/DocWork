package com.shpaginWork.docWork.repo;

import com.shpaginWork.docWork.models.Docs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocsRepository extends JpaRepository<Docs, Long> {

    Docs findBySender(String sender);
    Docs findByRecipient(String recipient);
    Docs save(Docs doc);
}
