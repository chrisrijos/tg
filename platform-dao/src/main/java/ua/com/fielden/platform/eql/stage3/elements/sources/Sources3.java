package ua.com.fielden.platform.eql.stage3.elements.sources;

import static java.util.Collections.emptyList;

import java.util.List;
import java.util.Objects;

public class Sources3 {
    public final IQrySource3 main;
    private final List<CompoundSource3> compounds;

    public Sources3(final IQrySource3 main, final List<CompoundSource3> compounds) {
        this.main = main;
        this.compounds = compounds;
    }

    public Sources3(final IQrySource3 main) {
        this(main, emptyList());
    }

    public List<CompoundSource3> getCompounds() {
        return compounds;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((compounds == null) ? 0 : compounds.hashCode());
        result = prime * result + ((main == null) ? 0 : main.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Sources3)) {
            return false;
        }

        final Sources3 other = (Sources3) obj;

        return Objects.equals(main, other.main) && Objects.equals(compounds, other.compounds);
    }
}