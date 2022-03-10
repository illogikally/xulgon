package me.min.xulgon.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@SuperBuilder
@NoArgsConstructor
public class Post extends Content {
   @ManyToOne
   private Content share;
   private Boolean hasShare;
   @OneToMany(mappedBy = "rootContent", fetch = FetchType.LAZY)
   private List<Comment> allComments;
}
