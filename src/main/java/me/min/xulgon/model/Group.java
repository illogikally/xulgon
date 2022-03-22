package me.min.xulgon.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

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
