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
public class Message {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @EqualsAndHashCode.Include
   private Long id;
   private String message;
   private Instant createdAt;
   private Boolean isRead;
   @ManyToOne
   private Conversation conversation;
   @ManyToOne
   private User sender;
   @ManyToOne
   private User receiver;
}

