package me.min.xulgon.mapper;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.PhotoResponse;
import me.min.xulgon.model.Photo;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Service;

@Mapper(componentModel = "spring")
public abstract class PhotoMapper {

   public abstract PhotoResponse toDto(Photo photo);

}
