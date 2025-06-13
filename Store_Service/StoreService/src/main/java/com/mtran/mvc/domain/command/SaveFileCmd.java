package com.mtran.mvc.domain.command;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SaveFileCmd {
  String name;
  String type;
  String path;
  String owner;
  Boolean visibility;
  Long size;
  String cloudinaryPublicId;
}
