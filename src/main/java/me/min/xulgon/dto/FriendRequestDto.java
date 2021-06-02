package me.min.xulgon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FriendRequestDto {
   private Long id;
   private Long requesterId;
   private String requesterAvatarUrl;
   private String requesterName;
   private String createdAgo;
   private Integer commonFriendCount;
}
