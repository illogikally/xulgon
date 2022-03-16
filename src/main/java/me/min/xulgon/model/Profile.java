package me.min.xulgon.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@SuperBuilder
@NoArgsConstructor
public class Profile extends Page {
   @OneToOne(fetch = FetchType.LAZY)
   private User user;
   @OneToOne
   private Photo avatar;
   @OneToOne
   private PhotoSet featuredPhotoSet;
   private String workplace;
   private String school;
   private String hometown;
}


