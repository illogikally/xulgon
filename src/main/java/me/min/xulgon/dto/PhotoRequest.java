package me.min.xulgon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.min.xulgon.model.Privacy;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhotoRequest {
   private String body;
   private Privacy privacy;
   private Long pageId;
   private Long parentId;
}
