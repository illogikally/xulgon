package me.min.xulgon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ThumbnailDto {
   private Long id;
   private Integer width;
   private Integer height;
   private String url;
}
