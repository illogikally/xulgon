package me.min.xulgon.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Photo extends Content {
   @ManyToOne
   private Content parentContent;
   @Enumerated(value = EnumType.STRING)
   private Privacy privacy;
   private String name;
   private Integer width;
   private Integer height;
   @OneToMany(mappedBy = "originalPhoto")
   private List<PhotoThumbnail> thumbnails;

   @Transient
   private Map<ThumbnailType, PhotoThumbnail> thumbnailsMap;

   @PostLoad
   private void map() {
      thumbnailsMap = thumbnails
            .stream()
            .collect(Collectors.toMap(PhotoThumbnail::getType, Function.identity()));
   }

   public Map<ThumbnailType, PhotoThumbnail> getThumbnailsMap() {
      return thumbnailsMap;
   }
}
