package me.min.xulgon.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Entity
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Photo extends Content {
   private String name;
   private Integer width;
   private Integer height;
   @OneToMany(
         mappedBy = "photo",
         cascade = CascadeType.REMOVE,
         orphanRemoval = true
   )
   private List<PhotoSetPhoto> photoSetPhotos;
}
