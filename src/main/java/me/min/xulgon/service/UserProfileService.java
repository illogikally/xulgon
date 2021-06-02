package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.UserProfileResponse;
import me.min.xulgon.mapper.UserProfileMapper;
import me.min.xulgon.model.UserProfile;
import me.min.xulgon.repository.UserProfileRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserProfileService {

   private final UserProfileRepository userProfileRepository;
   private final UserProfileMapper userProfileMapper;

   public UserProfileResponse getUserProfile(Long id) {
      UserProfile userProfile = userProfileRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Profile not found"));

      return userProfileMapper.toDto(userProfile);
   }

}
