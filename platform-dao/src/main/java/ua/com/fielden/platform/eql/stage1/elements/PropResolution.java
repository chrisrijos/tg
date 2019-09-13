package ua.com.fielden.platform.eql.stage1.elements;

import static java.util.Collections.unmodifiableList;

import java.util.List;
import java.util.Objects;

import ua.com.fielden.platform.eql.meta.AbstractPropInfo;
import ua.com.fielden.platform.eql.stage2.elements.sources.IQrySource2;
import ua.com.fielden.platform.eql.stage3.elements.sources.IQrySource3;

public class PropResolution {
    private final IQrySource2<? extends IQrySource3> source;
    private final List<AbstractPropInfo<?, ?>> path;

    public PropResolution(final IQrySource2<? extends IQrySource3> source, final List<AbstractPropInfo<?, ?>> path) {
        this.source = source;
        this.path = path;
    }
    public IQrySource2<? extends IQrySource3> getSource() {
        return source;
    }

    public List<AbstractPropInfo<?, ?>> getPath() {
        return unmodifiableList(path);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + source.hashCode();
        result = prime * result + path.hashCode();
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof PropResolution)) {
            return false;
        }
        
        final PropResolution other = (PropResolution) obj;
        
        return Objects.equals(source, other.source) && (path == other.path);
    }
}
