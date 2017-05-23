package ua.com.fielden.platform.web.layout;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ua.com.fielden.platform.dom.DomElement;
import ua.com.fielden.platform.web.interfaces.ILayout.Device;

public class FlexLayoutTest {

    @Test
    public void test_flex_layout_matching() {
        final FlexLayout flexLayout = new FlexLayout();
        flexLayout.whenMedia(Device.DESKTOP).set("[[[],[],[],[]],[[],[],[],[]]]")
                .whenMedia(Device.TABLET).set("[[[],[],[]],[[],[],[]],[[],[]]]")
                .whenMedia(Device.MOBILE).set("[[[],[]],[[],[]],[[],[]],[[],[]]]");
        final DomElement flexRendered = flexLayout.render();
        assertEquals("the number of children is incorrect", 0, flexRendered.childCount());
        assertEquals("the desktop layout constraints is incorrect", "[[_" + Device.DESKTOP.toString() + "Layout_" + flexLayout.hashCode() + "]]", flexRendered.getAttr("when-desktop").value);
        assertEquals("the tablet layout constraints is incorrect", "[[_" + Device.TABLET.toString() + "Layout_" + flexLayout.hashCode() + "]]", flexRendered.getAttr("when-tablet").value);
        assertEquals("the phone layout constraints is incorrect", "[[_" + Device.MOBILE.toString() + "Layout_" + flexLayout.hashCode() + "]]", flexRendered.getAttr("when-mobile").value);
    }
}
