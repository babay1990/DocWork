package com.shpaginWork.docWork.repo;

import com.shpaginWork.docWork.models.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {


}
