package me.min.xulgon.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Optional;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@SuperBuilder
@NoArgsConstructor
@Table(name = "user_profile")
public class UserPage extends Page {
   @OneToOne(fetch = FetchType.LAZY)
   private User user;
   @OneToOne
   private Photo avatar;

   private String workplace;
   private String school;
   private String hometown;

}


