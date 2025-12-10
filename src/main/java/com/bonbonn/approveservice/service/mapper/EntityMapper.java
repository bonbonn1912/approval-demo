package com.bonbonn.approveservice.service.mapper;

import com.bonbonn.approveservice.data.BookEntity;
import com.bonbonn.approveservice.data.DTO.AdditionalInformationDTO;
import com.bonbonn.approveservice.data.DTO.BookDTO;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EntityMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "approveType", constant = "UPDATE")
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "approvedBy", ignore = true)
  @Mapping(target = "draftTarget", source = "id")
  BookEntity copy(BookEntity bookEntity);

  List<BookDTO> toModel(List<BookEntity> bookEntities);

  @Mapping(target = "additionalInformation", source = ".")
  BookDTO toModel(BookEntity bookEntity);

  AdditionalInformationDTO toAdditionalInformation(BookEntity bookEntity);

}
