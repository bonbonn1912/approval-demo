package com.bonbonn.approveservice.service;

import com.bonbonn.approveservice.base.Constants;
import com.bonbonn.approveservice.data.ApproveType;
import com.bonbonn.approveservice.data.MovieEntity;
import com.bonbonn.approveservice.repository.MovieRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MovieService extends AbstractApprovableService<MovieEntity, MovieRepository> {

  private final MovieRepository movieRepository;

  protected MovieService(MovieRepository repository, MovieRepository movieRepository, ObjectMapper objectMapper) {
    super(repository,  objectMapper);
    this.movieRepository = movieRepository;
  }

  @Transactional
  public MovieEntity createMovie(String title, int lengthInMinutes, double price) {
    MovieEntity movieEntity = new MovieEntity();
    movieEntity.setApproveType(ApproveType.CREATE);
    movieEntity.setTitle(title);
    movieEntity.setLengthInMinutes(lengthInMinutes);
    movieEntity.setPrice(price);
    this.movieRepository.save(movieEntity);
    return movieEntity;
  }

  @Override
  protected TypeReference<MovieEntity> getTypeReference() {
    return new TypeReference<MovieEntity>() {};
  }
}
