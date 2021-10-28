package me.min.xulgon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.min.xulgon.model.GroupRole;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class GroupMemberDto {
   private UserDto user;
   private String avatarUrl;
   private String name;
   private GroupRole role;
}
