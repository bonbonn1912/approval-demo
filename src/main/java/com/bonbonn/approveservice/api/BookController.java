package com.bonbonn.approveservice.api;

import com.bonbonn.approveservice.data.DTO.BookDTO;
import com.bonbonn.approveservice.service.BookService;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/book")
@RequiredArgsConstructor
public class BookController {

  private final BookService bookService;

  @PostMapping
  public ResponseEntity<BookDTO> createBook(@RequestBody CreateBookRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(this.bookService.createBook(request));
  }

  @GetMapping
  public ResponseEntity<List<BookDTO>> getAllBooks(@RequestParam("includeDraft") boolean includeDraft){
    System.out.println("includeDraft: " + includeDraft);
    return ResponseEntity.ok(this.bookService.getAllBooks(includeDraft));
  }

  @PatchMapping("/{bookId}")
  public ResponseEntity<BookDTO> updateBook(@PathVariable Long bookId, @RequestBody CreateBookRequest bookEntity){
    return ResponseEntity.ok(this.bookService.updateBook(bookId, bookEntity));
  }

  @DeleteMapping("/{bookId}")
  public ResponseEntity<BookDTO> deleteBook(@PathVariable Long bookId){
    return ResponseEntity.ok(this.bookService.deleteBook(bookId));
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
  public static class CreateBookRequest {
    private String title;
    private int pages;
    private double price;
  }
}
