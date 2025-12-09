package com.bonbonn.approveservice.service;

import com.bonbonn.approveservice.base.Constants;
import com.bonbonn.approveservice.data.ApproveType;
import com.bonbonn.approveservice.data.BookEntity;
import com.bonbonn.approveservice.repository.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookService extends AbstractApprovableService<BookEntity, BookRepository> {

  private final BookRepository bookRepository;

  protected BookService(BookRepository repository, BookRepository bookRepository) {
    super(repository);
    this.bookRepository = bookRepository;
  }

  @Transactional
  public BookEntity createBook(String title, int pages, double price) {
    BookEntity bookEntity = new BookEntity();
    bookEntity.setApproveType(ApproveType.CREATE);
    bookEntity.setTitle(title);
    bookEntity.setPages(pages);
    bookEntity.setPrice(price);
    this.bookRepository.save(bookEntity);
    return bookEntity;
  }

  @Transactional
  public BookEntity updateBook(Long id, String title, int pages, double price) {
    BookEntity existing = bookRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + id));

    if(existing.getApprovedBy() == null) {
      throw new IllegalStateException("Book not approved yet. cannot update.");
    }

    BookEntity copy = new BookEntity();
    copy.setApproveType(ApproveType.UPDATE);
    copy.setTitle(title != null ? title : existing.getTitle());
    copy.setPages(pages > 0 ? pages : existing.getPages());
    copy.setPrice(price > 0 ? price : existing.getPrice());
    copy.setPredecessor(id);

    bookRepository.save(copy);

    return copy;
  }
}
