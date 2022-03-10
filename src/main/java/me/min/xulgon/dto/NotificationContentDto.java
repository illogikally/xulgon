package me.min.xulgon.dto;

import lombok.Builder;
import lombok.Data;
import me.min.xulgon.dto.UserBasicDto;
import me.min.xulgon.model.ContentType;

@Data
@Builder
public class NotificationContentDto {
   private Long id;
   private String text;
   private ContentType type;
   private UserBasicDto user;
}
