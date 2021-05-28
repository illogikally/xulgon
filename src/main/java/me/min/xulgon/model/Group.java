package me.min.xulgon.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.Table;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "group_table")
@AllArgsConstructor
@SuperBuilder
public class Group extends Page{

}
