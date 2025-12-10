package com.bonbonn.approveservice.service.mapper;

import com.bonbonn.approveservice.api.BookController.CreateBookRequest;
import com.bonbonn.approveservice.data.ApproveType;
import com.bonbonn.approveservice.data.BookEntity;
import com.bonbonn.approveservice.data.DTO.AdditionalInformationDTO;
import com.bonbonn.approveservice.data.DTO.BookDTO;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EntityMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "approvedBy", ignore = true)
  @Mapping(target = "draftTarget", source = "bookEntity.id")
  BookEntity copy(BookEntity bookEntity, ApproveType approveType);

  @Mapping(target = "approvedBy", ignore = true)
  BookEntity update(CreateBookRequest request, @MappingTarget BookEntity bookEntity);

  default BookEntity createUpdateDraft(BookEntity bookEntity, CreateBookRequest request){
    BookEntity draft = copy(bookEntity, ApproveType.UPDATE);
    return update(request, draft);
  }



  @Mapping(target = "approveType", constant = "CREATE")
  BookEntity fromRequest(CreateBookRequest request);

  List<BookDTO> toModel(List<BookEntity> bookEntities);

  @Mapping(target = "additionalInformation", source = ".")
  BookDTO toModel(BookEntity bookEntity);

  AdditionalInformationDTO toAdditionalInformation(BookEntity bookEntity);

}
