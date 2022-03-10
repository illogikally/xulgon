package me.min.xulgon.model;

import lombok.*;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PhotoThumbnail {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @EqualsAndHashCode.Include
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
