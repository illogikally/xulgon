package me.min.xulgon.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ReplyNotification extends Notification{
   @ManyToOne
   private Comment comment;
   @ManyToOne
   private Content target;
}
