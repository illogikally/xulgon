package me.min.xulgon.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Inheritance(strategy = InheritanceType.JOINED)
public class Content {
   @Id
   @GeneratedValue
   private Long id;
   @Lob
   private String body;
   @Enumerated(value = EnumType.STRING)
   private ContentType type;
   private Instant createdAt;

   @ManyToOne(fetch = FetchType.LAZY)
   private Page page;
   @ManyToOne(fetch = FetchType.LAZY)
   private User user;
   @OneToMany(mappedBy = "content")
   private List<Reaction> reactions;
   @OneToMany(mappedBy = "parent")
   private List<Comment> comments;

}
