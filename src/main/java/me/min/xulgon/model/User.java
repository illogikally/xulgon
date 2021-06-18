package me.min.xulgon.model;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
   @Id
   @GeneratedValue
   private Long id;
   private String username;
   private String password;
   private String firstName;
   private String lastName;
   private String email;
   private Instant createdAt;
   private boolean enabled;
   @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
   private UserProfile profile;
}
