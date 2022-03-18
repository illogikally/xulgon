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
   @ManyToOne(cascade = CascadeType.REMOVE)
   private NotificationSubject subject;

}
