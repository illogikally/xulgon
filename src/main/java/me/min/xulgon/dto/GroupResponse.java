package me.min.xulgon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupResponse {
   private Long id;
   private Boolean isMember;
   private Boolean isRequestSent;
   private String coverPhotoUrl;
   private String name;
   private Boolean isHidden;
   private Boolean isPrivate;
   private Integer memberCount;
   private String about;
}
