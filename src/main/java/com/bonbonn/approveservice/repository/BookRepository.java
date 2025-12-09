package com.bonbonn.approveservice.repository;

import com.bonbonn.approveservice.data.BookEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, Long> {

  List<BookEntity> findAllByApprovedByIsNotNull();
}
