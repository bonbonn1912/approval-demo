package com.bonbonn.approveservice.repository;

import com.bonbonn.approveservice.data.BookEntity;
import com.bonbonn.approveservice.data.MovieEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository<MovieEntity, Long> {

  List<MovieEntity> findAllByApprovedByIsNotNull();
}
