package me.min.xulgon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequest {
   private String message;
   private Long receiverId;
   private Long conversationId;
}
