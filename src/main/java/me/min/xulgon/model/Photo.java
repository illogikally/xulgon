package me.min.xulgon.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

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
