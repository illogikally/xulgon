package me.min.xulgon.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Friendship {
   @Id
   @GeneratedValue
   private Long id;
   private Instant createdAt;
   @ManyToOne
   private User userA;
   @ManyToOne
   private User userB;
}
