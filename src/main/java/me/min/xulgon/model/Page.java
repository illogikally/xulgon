package me.min.xulgon.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Inheritance(strategy = InheritanceType.JOINED)
public class Page {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;
   private String name;
   @Enumerated(value = EnumType.STRING)
   private PageType type;
   @OneToOne
   @Nullable
   private Photo coverPhoto;
   @OneToOne
   private PhotoSet pagePhotoSet;
   @OneToOne
   private PhotoSet avatarSet;
   @OneToOne
   private PhotoSet coverPhotoSet;
   @OneToMany(mappedBy = "page", fetch = FetchType.LAZY)
   private List<Follow> follows;
}
