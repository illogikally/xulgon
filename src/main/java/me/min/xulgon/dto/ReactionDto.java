package me.min.xulgon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.min.xulgon.model.ReactionType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReactionDto {
   private ReactionType type;
   private Long contentId;
}
