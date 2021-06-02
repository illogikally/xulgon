package me.min.xulgon.model;


import lombok.*;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContentEditHistory {
   @Id
   @GeneratedValue
   private Long id;
   private Instant createdAt;
   @ManyToOne
   private Content content;
   private String body;
}
