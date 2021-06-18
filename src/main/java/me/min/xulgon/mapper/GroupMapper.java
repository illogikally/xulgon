package me.min.xulgon.mapper;

import me.min.xulgon.dto.GroupRequest;
import me.min.xulgon.dto.GroupResponse;
import me.min.xulgon.model.Group;
import me.min.xulgon.model.GroupMember;
import me.min.xulgon.model.GroupRole;
import me.min.xulgon.model.User;
import me.min.xulgon.service.AuthenticationService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

@Mapper(componentModel = "spring",
      imports = {ArrayList.class})
public abstract class GroupMapper {

   @Autowired
   AuthenticationService authService;

   @Mapping(target = "id", ignore = true)
   @Mapping(target = "type", constant = "GROUP")
   @Mapping(target = "members", expression = "java(new ArrayList<>())")
   public abstract Group map(GroupRequest groupRequest);


   @Mapping(target = "coverPhotoUrl", expression = "java(getPhotoUrl(group))")
   @Mapping(target = "memberCount"  , expression = "java(group.getMembers().size())")
   @Mapping(target = "isMember"     , expression = "java(isMember(group))")
   @Mapping(target = "isRequestSent", expression = "java(isRequestSent(group))")
   @Mapping(target = "role"         , expression = "java(getRole(group))")
   public abstract GroupResponse toDto(Group group);

   String getPhotoUrl(Group group) {
      if (group.getCoverPhoto() == null) return null;

      return group.getCoverPhoto().getUrl();
   }

   GroupRole getRole(Group group) {
      return group.getMembers()
            .stream()
            .filter(member ->
                  member.getUser().getId().equals(authService.getLoggedInUser().getId()))
            .map(GroupMember::getRole)
            .findAny()
            .orElse(null);

   }

   Boolean isRequestSent(Group group) {
      User user = authService.getLoggedInUser();
      return group.getJoinRequests()
            .stream()
            .anyMatch(request -> request.getUser().getId().equals(user.getId()));
   }

   Boolean isMember(Group group) {
      User user = authService.getLoggedInUser();
      return group.getMembers()
            .stream()
            .anyMatch(member -> member.getUser().getId().equals(user.getId()));

   }

}
