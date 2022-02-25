package me.min.xulgon.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Lazy;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Inheritance(strategy = InheritanceType.JOINED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Content {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @EqualsAndHashCode.Include
   private Long id;
   @Lob
   private String body;
   @Enumerated(value = EnumType.STRING)
   private ContentType type;
   private Instant createdAt;
   private Integer commentCount;
   private Integer reactionCount;

   @ManyToOne(fetch = FetchType.LAZY)
   private Page page;
   @ManyToOne(fetch = FetchType.LAZY)
   @NotNull
   private User user;
   @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
   private List<Reaction> reactions;
   @OneToMany(mappedBy = "parentContent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
   private List<Comment> comments;
   @OneToMany(mappedBy = "parentContent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
   private List<Photo> photos;
}
