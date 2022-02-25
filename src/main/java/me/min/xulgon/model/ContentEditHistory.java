package me.min.xulgon.model;


import lombok.*;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
public class ContentEditHistory {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @EqualsAndHashCode.Include
   private Long id;
   private Instant createdAt;
   @ManyToOne
   private Content content;
   private String body;
}
