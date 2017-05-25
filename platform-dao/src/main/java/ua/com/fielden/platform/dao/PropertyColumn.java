package ua.com.fielden.platform.dao;

public class PropertyColumn {
    private final String name;
    private final Integer length;
    private final Integer precision;
    private final Integer scale;

    public PropertyColumn(final String name, final Integer length, final Integer precision, final Integer scale) {
        super();
        this.name = name.toUpperCase();
        this.length = length;
        this.precision = precision;
        this.scale = scale;
    }

    public PropertyColumn(final String name) {
        this(name, null, null, null);
    }

    public String ddl() {
        return name.toUpperCase() + " INT";
    }

    public String getName() {
        return name;
    }

    public Integer getLength() {
        return length;
    }

    public Integer getPrecision() {
        return precision;
    }

    public Integer getScale() {
        return scale;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((length == null) ? 0 : length.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((precision == null) ? 0 : precision.hashCode());
        result = prime * result + ((scale == null) ? 0 : scale.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof PropertyColumn)) {
            return false;
        }
        final PropertyColumn other = (PropertyColumn) obj;
        if (length == null) {
            if (other.length != null) {
                return false;
            }
        } else if (!length.equals(other.length)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (precision == null) {
            if (other.precision != null) {
                return false;
            }
        } else if (!precision.equals(other.precision)) {
            return false;
        }
        if (scale == null) {
            if (other.scale != null) {
                return false;
            }
        } else if (!scale.equals(other.scale)) {
            return false;
        }
        return true;
    }
}