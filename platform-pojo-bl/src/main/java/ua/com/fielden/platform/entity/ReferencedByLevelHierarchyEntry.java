package ua.com.fielden.platform.entity;

import ua.com.fielden.platform.entity.annotation.DescTitle;
import ua.com.fielden.platform.entity.annotation.IsProperty;
import ua.com.fielden.platform.entity.annotation.KeyTitle;
import ua.com.fielden.platform.entity.annotation.Observable;
import ua.com.fielden.platform.entity.annotation.Title;

@KeyTitle("Entity key")
@DescTitle("Entity Description")
public class ReferencedByLevelHierarchyEntry extends ReferenceHierarchyEntry {

    @IsProperty
    @Title(value = "Entity", desc = "Entity refrence")
    private AbstractEntity<?> entity;

    @Observable
    public ReferencedByLevelHierarchyEntry setEntity(final AbstractEntity<?> entity) {
        this.entity = entity;
        return this;
    }

    public AbstractEntity<?> getEntity() {
        return entity;
    }
}
