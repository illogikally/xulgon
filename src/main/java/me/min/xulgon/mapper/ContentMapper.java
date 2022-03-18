package me.min.xulgon.mapper;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.NotificationContentDto;
import me.min.xulgon.dto.PhotoResponse;
import me.min.xulgon.dto.ContentDto;
import me.min.xulgon.model.Content;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ContentMapper {

   private UserMapper userMapper;
   private PhotoMapper photoMapper;

   public ContentDto toDto(Content content) {
      if (content == null) return null;

      List<PhotoResponse> photos = content.getPhotos()
            .stream()
            .map(photoMapper::toPhotoResponse)
            .collect(Collectors.toList());

      return ContentDto.builder()
            .id(content.getId())
            .text(content.getBody())
            .type(content.getType())
            .createdAt(MappingUtil.getCreatedAt(content.getCreatedAt()))
            .user(userMapper.toDto(content.getUser()))
            .pageId(content.getPage().getId())
            .photoCount(photos.size())
            .photoSetId(content.getPhotoSet().getId())
            .pageName(content.getPage().getName())
            .privacy(content.getPrivacy())
            .pageType(content.getPage().getType())
            .photos(photos.stream().limit(4).collect(Collectors.toList()))
            .build();
   }

}
