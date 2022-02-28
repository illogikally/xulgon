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
   @ManyToOne
   private Content parentContent;
   private String name;
   @Enumerated(value = EnumType.STRING)
   private Privacy privacy;
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
