package me.min.xulgon.dto;

import lombok.*;
import me.min.xulgon.util.LimPageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

import java.util.List;

@Data
@Builder
public class PageableResponse<T> {
   private Boolean hasNext;
   private Integer size;
   private Long offset;
   private List<T> data;

   public static <T> PageableResponse<T> empty() {
      return PageableResponse
            .<T>builder()
            .hasNext(false)
            .offset(0L)
            .data(List.of())
            .size(0)
            .build();
   }
}
