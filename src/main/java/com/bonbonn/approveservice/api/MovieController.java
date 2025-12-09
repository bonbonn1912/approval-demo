package com.bonbonn.approveservice.api;


import com.bonbonn.approveservice.data.MovieEntity;
import com.bonbonn.approveservice.repository.MovieRepository;
import com.bonbonn.approveservice.service.MovieService;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/movie")
@RequiredArgsConstructor
public class MovieController {

  private final MovieService movieService;
  private final MovieRepository movieRepository;

  @PostMapping
  public ResponseEntity<MovieEntity> createMovie(@RequestBody CreateMovieRequest request) {
    MovieEntity created = this.movieService.createMovie(
        request.getTitle(),
        request.getLengthInMinutes(),
        request.getPrice()
    );
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @GetMapping
  public ResponseEntity<List<MovieEntity>> getAllApprovedMovies() {
    return ResponseEntity.ok(this.movieRepository.findAllByApprovedByIsNotNull());
  }

  @PostMapping("/{movieId}/approve")
  public ResponseEntity<Void> approveMovie(@PathVariable Long movieId) {
    this.movieService.approve(movieId);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{movieId}/reject")
  public ResponseEntity<Void> rejectMovie(@PathVariable Long movieId) {
    this.movieService.reject(movieId);
    return ResponseEntity.noContent().build();
  }

  @Getter
  static class CreateMovieRequest {
    private String title;
    private int lengthInMinutes;
    private double price;
  }
}
