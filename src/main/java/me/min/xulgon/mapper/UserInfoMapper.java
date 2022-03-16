package me.min.xulgon.mapper;

import me.min.xulgon.dto.UserInfoDto;
import me.min.xulgon.model.User;
import me.min.xulgon.model.UserInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class UserInfoMapper {
   @Mapping(target = "userId", source = "userInfo.user.id")
   public abstract UserInfoDto toDto(UserInfo userInfo);

   @Mapping(target = "id", source = "user.userInfo.id")
   public abstract UserInfo toUserInfo(UserInfoDto dto, User user);
}
