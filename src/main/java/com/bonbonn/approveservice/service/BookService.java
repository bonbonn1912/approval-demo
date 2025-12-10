package com.bonbonn.approveservice.service;

import com.bonbonn.approveservice.data.ApproveType;
import com.bonbonn.approveservice.data.BookEntity;
import com.bonbonn.approveservice.data.DTO.BookDTO;
import com.bonbonn.approveservice.repository.BookRepository;
import com.bonbonn.approveservice.service.mapper.EntityMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookService extends AbstractApprovableService<BookEntity, BookRepository> {
  private final BookRepository bookRepository;
  private final EntityMapper mapper;


  protected BookService(BookRepository repository, BookRepository bookRepository, ObjectMapper objectMapper,
      EntityMapper mapper) {
    super(repository,  objectMapper);
    this.bookRepository = bookRepository;
    this.mapper = mapper;
  }

  @Transactional
  public BookDTO createBook(String title, int pages, double price) {
    BookEntity bookEntity = new BookEntity();
    bookEntity.setApproveType(ApproveType.CREATE);
    bookEntity.setTitle(title);
    bookEntity.setPages(pages);
    bookEntity.setPrice(price);
    addAuditDetails(bookEntity);
    this.bookRepository.save(bookEntity);
    return this.mapper.toModel(bookEntity);
  }

  @Transactional
  public BookDTO updateBook(Long id, String title, int pages, double price) {
    BookEntity existing = bookRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + id));

    if(existing.getApprovedBy() == null) {
      throw new IllegalStateException("Book not approved yet. cannot update.");
    }

    BookEntity draft = this.mapper.copy(existing);
    draft.setTitle(title);
    draft.setPages(pages);
    draft.setPrice(price);
    setDraft(existing, draft);
    this.bookRepository.save(existing);
    return this.mapper.toModel(existing);
  }

  public List<BookDTO> getAllBooks(boolean includeDraft) {
    List<BookEntity> bookEntities = bookRepository.findAll();
    if(includeDraft) {
      return mapper.toModel(getOpenDrafts(bookEntities));
    }
    return bookEntities.stream()
        .filter(book -> Objects.nonNull(book.getApprovedBy()))
        .map(mapper::toModel)
        .toList();
  }

  @Override
  protected TypeReference<BookEntity> getTypeReference() {
    return new TypeReference<BookEntity>() {};
  }
}
