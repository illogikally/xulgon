package me.min.xulgon.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Follow {
   @Id
   @GeneratedValue
   private Long id;
   private Instant createdAt;
   @ManyToOne
   private User user;
   @ManyToOne
   private Page page;
   @ManyToOne
   private Content content;
}
