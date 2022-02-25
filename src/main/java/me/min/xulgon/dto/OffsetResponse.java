package me.min.xulgon.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
public class OffsetResponse<T> {
   private Boolean hasNext;
   private Integer size;
   private Long offset;
   private List<T> data;

   public static <T> OffsetResponse<T> empty() {
      return OffsetResponse
            .<T>builder()
            .hasNext(false)
            .offset(0L)
            .data(List.of())
            .size(0)
            .build();
   }
}
