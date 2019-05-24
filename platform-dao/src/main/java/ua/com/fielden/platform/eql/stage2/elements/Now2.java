package ua.com.fielden.platform.eql.stage2.elements;

import org.joda.time.DateTime;

public class Now2 extends ZeroOperandFunction2 {
    public Now2() {
        super("now");
    }

    @Override
    public String type() {
        return DateTime.class.getName();
    }
}