package me.min.xulgon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.min.xulgon.model.Privacy;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostRequest {
   private String body;
   private Privacy privacy;
   private Long sharedPostId;
   private Long pageId;
}
