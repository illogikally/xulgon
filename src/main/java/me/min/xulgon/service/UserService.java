package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.UserDto;
import me.min.xulgon.mapper.UserMapper;
import me.min.xulgon.model.Photo;
import me.min.xulgon.model.User;
import me.min.xulgon.model.UserProfile;
import me.min.xulgon.repository.PhotoRepository;
import me.min.xulgon.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {
   private final UserRepository userRepository;
   private final FriendshipService friendshipService;
   private final UserMapper userMapper;
   private final PhotoRepository photoRepository;

   public List<UserDto> getFriends(Long userId) {
      User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
      return friendshipService.getFriends(user).stream()
            .map(userMapper::toDto)
            .collect(Collectors.toList());
   }

}
