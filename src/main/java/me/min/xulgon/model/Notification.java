package me.min.xulgon.model;


import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Notification {
   @Id
   @GeneratedValue
   @EqualsAndHashCode.Include
   private Long id;
   private Instant createdAt;
   @Enumerated(value = EnumType.STRING)
   private NotificationType type;
   private Boolean isRead;
   @ManyToOne
   private Page page;
   @ManyToOne
   private User actor;
   @ManyToOne
   private User recipient;
   @ManyToOne
   private Content recipientContent;
   @ManyToOne
   private Content actorContent;
}
