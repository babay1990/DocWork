package com.shpaginWork.docWork.repo;

import com.shpaginWork.docWork.models.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    News findByTitle(String title);
    News save(News news);
}
