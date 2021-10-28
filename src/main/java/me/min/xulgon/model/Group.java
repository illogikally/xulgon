package me.min.xulgon.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "group_table")
@AllArgsConstructor
@SuperBuilder
@NoArgsConstructor
public class Group extends Page{
   private Boolean isHidden;
   private Boolean isPrivate;
   private String intro;
   private String name;

   @OneToMany(mappedBy = "group")
   private List<GroupMember> members;
   @OneToMany(mappedBy = "group")
   private List<GroupJoinRequest> joinRequests;
}
