package me.min.xulgon.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Reaction {
   @Id
   @GeneratedValue
   private Long id;
   @Enumerated(value = EnumType.STRING)
   private ReactionType type;
   @ManyToOne
   private Content content;
   @ManyToOne
   private User user;
}
