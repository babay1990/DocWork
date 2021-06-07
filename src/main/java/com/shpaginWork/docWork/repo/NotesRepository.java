package com.shpaginWork.docWork.repo;

import com.shpaginWork.docWork.models.Notes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotesRepository extends JpaRepository<Notes, Long> {
}
