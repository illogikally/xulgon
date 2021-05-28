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
public class GroupMember {
   @Id
   @GeneratedValue
   private Long id;
   private Instant createdAt;
   private GroupRole role;
   @ManyToOne
   private User user;
   @ManyToOne
   private Group group;
}
