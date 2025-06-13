package com.mtran.mvc.domain;

import com.mtran.mvc.domain.command.SaveFileCmd;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class File {
    UUID id;
    String name;
    String type;
    String path;
    String owner;
    Boolean visibility;
    Long size;
    String cloudinaryPublicId;

    public File(SaveFileCmd saveFileCmd) {
        this.id = UUID.randomUUID();
        this.name = saveFileCmd.getName();
        this.type = saveFileCmd.getType();
        this.path = saveFileCmd.getPath();
        this.owner = saveFileCmd.getOwner();
        this.visibility = saveFileCmd.getVisibility();
        this.size = saveFileCmd.getSize();
        this.cloudinaryPublicId = saveFileCmd.getCloudinaryPublicId();
    }

    public void updateFile(String newName, String newPath) {
        this.name = newName;
        this.path = newPath;
    }
}
