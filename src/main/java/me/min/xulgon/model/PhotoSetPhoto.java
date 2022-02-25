package me.min.xulgon.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PhotoSetPhoto {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;
   private Instant createdAt;
   private Integer photoIndex;
   private Boolean hasNext;
   @ManyToOne
   private PhotoSet photoSet;
   @ManyToOne
   private Photo photo;
}
