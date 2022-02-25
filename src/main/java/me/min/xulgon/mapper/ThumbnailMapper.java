package me.min.xulgon.mapper;

import me.min.xulgon.dto.ThumbnailDto;
import me.min.xulgon.model.PhotoThumbnail;
import me.min.xulgon.util.Util;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

@Mapper(componentModel = "spring", imports = {Util.class})
public abstract class ThumbnailMapper {
   @Autowired
   Environment env;

   @Mapping(target = "url", expression = "java(Util.getThumbnailUrl(env, thumbnail))")
   public abstract ThumbnailDto toDto(PhotoThumbnail thumbnail);
}
