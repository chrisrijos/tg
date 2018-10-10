package ua.com.fielden.platform.web.interfaces;


public interface ILayout<T extends ILayoutSetter<?>> {

    /**
     * Specifies the device and orientation for which the specific layout must be set.
     *
     * @param device
     * @param orientation
     * @return
     */
    public T whenMedia(final Device device, final Orientation orientation);

    /**
     * Specifies the device for which the specific layout must be set.
     *
     * @param device
     * @return
     */
    public T whenMedia(final Device device);

    /**
     * Represents the list of supported devices.
     *
     * @author TG Team
     *
     *         - a component to be added to layout manager.
     */
    public static enum Device {
        DESKTOP,
        TABLET,
        MOBILE,
        PRINT;
        @Override
        public String toString() {
            return name().toLowerCase();
        };
    }

    /**
     * Represents the device orientation.
     *
     * @author TG Team
     *
     */
    public static enum Orientation {
        LANDSCAPE,
        PORTRAIT;
        @Override
        public String toString() {
            return name().toLowerCase();
        };
    }
}
