package me.min.xulgon.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class CommentNotification extends Notification {
   @OneToOne
   private Post post;
   @OneToOne
   private Comment comment;
}
