package ua.com.fielden.platform.eql.stage1.elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ua.com.fielden.platform.eql.stage2.elements.GroupBy2;
import ua.com.fielden.platform.eql.stage2.elements.GroupBys2;

public class GroupBys1 {
    private final List<GroupBy1> groups;

    public GroupBys1(final List<GroupBy1> groups) {
        this.groups = groups;
    }

    public TransformationResult<GroupBys2> transform(final PropsResolutionContext context, final String sourceId) {
        final List<GroupBy2> transformed = new ArrayList<>();
        PropsResolutionContext currentResolutionContext = context;
        for (final GroupBy1 groupBy : groups) {
            final TransformationResult<GroupBy2> groupByTr = groupBy.transform(currentResolutionContext, sourceId);
            transformed.add(groupByTr.item);
            currentResolutionContext = groupByTr.updatedContext;
        }
        return new TransformationResult<GroupBys2>(new GroupBys2(transformed), currentResolutionContext);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + groups.hashCode();
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof GroupBys1)) {
            return false;
        }

        final GroupBys1 other = (GroupBys1) obj;
        
        return Objects.equals(groups, other.groups);
    }
}