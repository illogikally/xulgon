package me.min.xulgon.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Inheritance(strategy = InheritanceType.JOINED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Content {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @EqualsAndHashCode.Include
   private Long id;
   @Lob
   private String body;
   @Enumerated(value = EnumType.STRING)
   private ContentType type;
   private Instant createdAt;
   private Integer commentCount;
   private Integer reactionCount;
   private Integer shareCount;
   @Enumerated(value = EnumType.STRING)
   private Privacy privacy;
   @OneToOne(cascade = CascadeType.REMOVE)
   private PhotoSet photoSet;
   @ManyToOne(fetch = FetchType.LAZY)
   private Content parentContent;
   @ManyToOne(fetch = FetchType.LAZY)
   private Page page;
   @ManyToOne(fetch = FetchType.LAZY)
   @NotNull
   private User user;
   @OneToMany(mappedBy = "content", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
   private List<Reaction> reactions;
   @OneToMany(mappedBy = "parentContent", fetch = FetchType.LAZY)
   private List<Content> children;
   @OneToMany(mappedBy = "share", fetch = FetchType.LAZY)
   private List<Post> shares;
   @OneToMany(mappedBy = "content", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE})
   private List<Follow> follows;

   @OneToMany(mappedBy = "subjectContent", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
   private List<NotificationSubject> notificationSubjects;
//   @OneToMany(mappedBy = "rootContent", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
//   private List<NotificationSubject> childNotificationSubjects;
   @OneToMany(mappedBy = "actorContent", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
   private List<Notification> notifications;

   @Transient
   private List<Photo> photos;

   public List<Photo> getPhotos() {
      if (photos == null) {
         return photoSet.getPhotoSetPhotos()
               .stream()
               .map(PhotoSetPhoto::getPhoto)
               .collect(Collectors.toList());
      }
      return photos;
   }

   public boolean isType(ContentType type) {
      return this.type.equals(type);
   }

   public boolean isNotType(ContentType type) {
      return !isType(type);
   }
}
