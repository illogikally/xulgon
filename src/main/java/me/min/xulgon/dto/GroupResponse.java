package me.min.xulgon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.min.xulgon.model.GroupRole;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupResponse {
   private Long id;
   private Boolean isMember;
   private GroupRole role;
   private Boolean isRequestSent;
   private String coverPhotoUrl;
   private String coverLeftColor;
   private String coverRightColor;
   private String name;
   private Boolean isHidden;
   private Boolean isPrivate;
   private Boolean isFollow;
   private Integer memberCount;
   private String about;
}
