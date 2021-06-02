package me.min.xulgon.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.Instant;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Block {
   @Id
   @GeneratedValue
   private Long id;
   @ManyToOne
   private User blocker;
   @ManyToOne
   private User blockee;
   private Instant createdAt;
}
