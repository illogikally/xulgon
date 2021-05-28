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
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendRequest {
   @Id
   @GeneratedValue
   private Long id;
   private Instant createdAt;

   @ManyToOne
   private User requestor;
   @ManyToOne
   private User requestee;

}
