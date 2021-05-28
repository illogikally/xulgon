package me.min.xulgon.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

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
   @Enumerated(value = EnumType.STRING)
   private PageType type;
   private boolean isPrivate;
   @OneToOne
   private Photo coverPhoto;

}
