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
public class GroupMember {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;
   private Instant createdAt;
   @Enumerated(EnumType.STRING)
   private GroupRole role;
   @ManyToOne
   private User user;
   @ManyToOne
   private Group group;
}
