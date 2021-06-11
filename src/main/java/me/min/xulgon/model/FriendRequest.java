package me.min.xulgon.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
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

   @ManyToOne(fetch = FetchType.LAZY)
   private User requester;
   @ManyToOne(fetch = FetchType.LAZY)
   private User requestee;

}
