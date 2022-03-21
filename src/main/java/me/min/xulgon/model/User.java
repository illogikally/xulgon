package me.min.xulgon.model;

import lombok.*;
import javax.persistence.*;
import java.time.Instant;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "user_table")
public class User {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @EqualsAndHashCode.Include
   private Long id;
   private String username;
   private String password;
   private String firstName;
   private String lastName;
   private String fullName;
   private String email;
   @Enumerated(value = EnumType.STRING)
   private Provider provider;
   private String oauth2Name;
   private Instant createdAt;
   private Integer unreadMessageCount;
   private Integer unreadNotificationCount;
   private Boolean enabled;
   @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
   private Profile profile;
   @OneToOne
   private UserInfo userInfo;
}
