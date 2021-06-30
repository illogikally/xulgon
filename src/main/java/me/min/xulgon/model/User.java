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
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "id")
   private Long id;
   private String username;
   private String password;
   private String firstName;
   private String lastName;
   private String fullName;
   private Instant createdAt;
   private Integer unreadMessageCount;
   private Integer unreadNotificationCount;
   private Boolean enabled;
   @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
   private UserProfile profile;
}
