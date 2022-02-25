package me.min.xulgon.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PhotoSet {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;
   private Instant createdAt;
   @Enumerated(value = EnumType.STRING)
   private SetType type;
   @ManyToOne
   private Page page;
   @OneToMany(fetch = FetchType.LAZY)
   private List<PhotoSetPhoto> photoSetPhoto;

   public static PhotoSet generate(SetType type) {
      return PhotoSet.builder()
            .type(type)
            .createdAt(Instant.now())
            .build();
   }
}
