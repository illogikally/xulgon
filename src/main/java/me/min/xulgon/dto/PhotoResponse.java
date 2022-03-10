package me.min.xulgon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.min.xulgon.model.ThumbnailType;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PhotoResponse {
   private Long id;
   private String url;
   private Map<ThumbnailType, ThumbnailDto> thumbnails;
}
