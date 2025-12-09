package com.bonbonn.approveservice.api;

import com.bonbonn.approveservice.data.BookEntity;
import com.bonbonn.approveservice.repository.BookRepository;
import com.bonbonn.approveservice.service.BookService;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/book")
@RequiredArgsConstructor
public class BookController {

  private final BookService bookService;
  private final BookRepository bookRepository;

  @PostMapping
  public ResponseEntity<BookEntity> createBook(@RequestBody CreateBookRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(this.bookService.createBook(request.getTitle(), request.getPages(), request.getPrice()));
  }

  @GetMapping
  public ResponseEntity<List<BookEntity>> getAllApprovedBook(){
    return ResponseEntity.ok(this.bookRepository.findAllByApprovedByIsNotNull());
  }

  @PatchMapping("/{bookId}")
  public ResponseEntity<BookEntity> updateBook(@PathVariable Long bookId, @RequestBody CreateBookRequest bookEntity){
    return ResponseEntity.ok(this.bookService.updateBook(bookId, bookEntity.getTitle(), bookEntity.getPages(), bookEntity.getPrice()));
  }

  @PostMapping("/{bookId}/approve")
  public ResponseEntity<Void> approveBook(@PathVariable Long bookId){
    this.bookService.approve(bookId);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{bookId}/reject")
  public ResponseEntity<Void> rejectBook(@PathVariable Long bookId){
    this.bookService.reject(bookId);
    return ResponseEntity.noContent().build();
  }

  @Getter
  static class CreateBookRequest {
    private String title;
    private int pages;
    private double price;
  }
}
