package me.min.xulgon.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@SuperBuilder
@NoArgsConstructor
public class Post extends Content {
   @ManyToOne
   private Post sharedPost;
   @OneToOne
   private PhotoSet photoSet;
   @Enumerated(value = EnumType.STRING)
   private Privacy privacy;
}
