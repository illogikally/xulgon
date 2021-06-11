package me.min.xulgon.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Photo extends Content {
   @ManyToOne
   @ToString.Exclude
   private Content parent;
   @Enumerated(value = EnumType.STRING)
   private Privacy privacy;
   private String url;
   private Float sizeRatio;
}
