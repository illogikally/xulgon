package me.min.xulgon.model;

import com.sun.mail.imap.protocol.INTERNALDATE;
import lombok.*;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class NotificationSubject {
   @Id
   @GeneratedValue
   @EqualsAndHashCode.Include
   private Long id;
   private Integer actorCount;
   private Instant latestCreatedAt;
   private Boolean isRead;
   @Enumerated(value = EnumType.STRING)
   private NotificationType type;
   @ManyToOne
   private Notification latestNotification;
   @ManyToOne
   private User recipient;
   @ManyToOne
   private Page page;
   @ManyToOne
   private Content subjectContent;
   @OneToMany(mappedBy = "subject", fetch = FetchType.LAZY)
   private List<Notification> notifications;

}
