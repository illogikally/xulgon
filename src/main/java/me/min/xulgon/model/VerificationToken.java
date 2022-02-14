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
public class VerificationToken {
   @Id
   @GeneratedValue
   private Long id;
   private String token;
   @OneToOne
   private User user;
   private Instant expiryDate;
}
