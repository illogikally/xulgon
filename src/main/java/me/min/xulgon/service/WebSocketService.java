package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.WebSocketContentDto;
import me.min.xulgon.dto.WebSocketContentType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class WebSocketService {
   private final SimpMessagingTemplate simpMessagingTemplate;

   public <T> void  send(Long parentId,
                         WebSocketContentType type,
                         T content,
                         String topic) {
      WebSocketContentDto<T> dto = WebSocketContentDto
            .<T>builder()
            .content(content)
            .parentId(parentId)
            .type(type)
            .build();
      simpMessagingTemplate.convertAndSend(topic, dto);
   }
}
