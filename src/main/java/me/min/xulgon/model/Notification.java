package me.min.xulgon.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notification {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;
   private Instant createdAt;
   @Enumerated(value = EnumType.STRING)
   private NotificationType type;
   private Boolean isRead;
   @ManyToOne
   private Content content;
   @ManyToOne
   private Page page;
   @ManyToOne
   private User actor;
   @ManyToOne
   private User recipient;
}
