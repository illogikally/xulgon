package me.min.xulgon.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WebSocketContentDto<T> {
   private Long parentId;
   private WebSocketContentType type;
   private T content;
}
