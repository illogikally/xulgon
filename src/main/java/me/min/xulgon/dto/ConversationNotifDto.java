package me.min.xulgon.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConversationNotifDto {
    private Long id;
    private MessageResponse latestMessage;
    private UserBasicDto user;
}
