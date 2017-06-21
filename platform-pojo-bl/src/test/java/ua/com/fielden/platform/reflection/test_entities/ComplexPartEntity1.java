package ua.com.fielden.platform.reflection.test_entities;

import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.annotation.DescTitle;
import ua.com.fielden.platform.entity.annotation.IsProperty;
import ua.com.fielden.platform.entity.annotation.KeyTitle;
import ua.com.fielden.platform.entity.annotation.KeyType;
import ua.com.fielden.platform.entity.annotation.Observable;
import ua.com.fielden.platform.entity.annotation.Title;

@KeyType(UnionEntityForReflector.class)
@KeyTitle(value = "key", desc = "key description")
@DescTitle(value = "description", desc = "desc description")
public class ComplexPartEntity1 extends AbstractEntity<UnionEntityForReflector> {

    @IsProperty
    @Title(value = "Common property", desc = "Common property description")
    private String commonProperty;

    @IsProperty
    @Title(value = "Level property", desc = "Level property description")
    private SecondLevelEntity levelEntity;

    @IsProperty
    @Title(value = "Uncommon property", desc = "Uncommon property description")
    private String anotherUncommonProperty;

    public String getCommonProperty() {
        return commonProperty;
    }

    @Observable
    public void setCommonProperty(final String commonProperty) {
        this.commonProperty = commonProperty;
    }

    public SecondLevelEntity getLevelEntity() {
        return levelEntity;
    }

    @Observable
    public void setLevelEntity(final SecondLevelEntity levelEntity) {
        this.levelEntity = levelEntity;
    }

    public String getAnotherUncommonProperty() {
        return anotherUncommonProperty;
    }

    @Observable
    public void setAnotherUncommonProperty(final String anotherUncommonProperty) {
        this.anotherUncommonProperty = anotherUncommonProperty;
    }

}
