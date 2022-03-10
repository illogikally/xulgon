package me.min.xulgon.model;


import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Notification {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @EqualsAndHashCode.Include
   private Long id;
   private Instant createdAt;
   @ManyToOne
   private User actor;
   @ManyToOne
   private Content actorContent;
   @ManyToOne
   private NotificationSubject subject;

   public static Notification generate(Content actorContent) {
      return Notification.builder()
            .createdAt(Instant.now())
            .actor(actorContent.getUser())
            .actorContent(actorContent)
            .build();
   }
}
