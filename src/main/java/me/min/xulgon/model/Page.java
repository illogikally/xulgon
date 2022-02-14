package me.min.xulgon.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.util.Optional;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Inheritance(strategy = InheritanceType.JOINED)
public class Page {
   @Id
   @GeneratedValue
   private Long id;
   private String name;
   @Enumerated(value = EnumType.STRING)
   private PageType type;
   @OneToOne
   @Nullable
   private Photo coverPhoto;

   public Optional<Photo> getCoverPhoto() {
      return Optional.ofNullable(coverPhoto);
   }
}
