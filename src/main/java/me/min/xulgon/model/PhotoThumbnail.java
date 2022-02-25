package me.min.xulgon.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PhotoThumbnail {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;
   @Enumerated(value = EnumType.STRING)
   private ThumbnailType type;
   private Integer width;
   private Integer height;
   private String name;
   private String url;
   @ManyToOne
   private Photo originalPhoto;
}
