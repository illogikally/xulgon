package me.min.xulgon.model;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;

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
   @OneToOne(fetch = FetchType.LAZY)
   @EqualsAndHashCode.Exclude
   private Photo avatar;
   @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
   @EqualsAndHashCode.Exclude
   private UserProfile profile;
}
