package ua.com.fielden.platform.web.app;

import java.io.InputStream;

import ua.com.fielden.platform.web.interfaces.DeviceProfile;

/**
 * The contract for loading resources by their URIs of filePaths.
 * <p>
 * The implementation governs the loading in such a way that preloaded import URIs are excluded from the source not to be imported twice (this behavior works
 * in case of deployment server mode).
 *
 * @author TG Team
 *
 */
public interface ISourceController {
    /**
     * Loads the text representation of the resource with the specified 'resourceURI'.
     * <p>
     * Please, note that the resources should be accessed through the '/resources' prefix or one of the prefixes for generated resources: '/app', '/master_ui' or '/centre_ui'.
     * <p>
     * The implementation governs the loading in such a way that preloaded import URIs are excluded from the source not to be imported twice (this behavior works
     * in case of deployment server mode).
     *
     * @param resourceURI
     * @param deviceProfile indicates the profile for the web app device (preloaded resources for different deviceProfiles could be different)
     *
     * @return
     */
    String loadSource(final String resourceURI, final DeviceProfile deviceProfile);

    /**
     * Loads the text representation of the resource with the specified 'filePath'.
     * <p>
     * The implementation governs the loading in such a way that preloaded import URIs are excluded from the source not to be imported twice (this behavior works
     * in case of deployment server mode).
     *
     * @param resourceURI
     * @param deviceProfile indicates the profile for the web app device (preloaded resources for different deviceProfiles could be different)
     *
     * @return
     */
    String loadSourceWithFilePath(final String filePath, final DeviceProfile deviceProfile);

    /**
     * Loads input stream representation of the resource with the specified 'filePath'.
     *
     * @param resourceURI
     *
     * @return
     */
    InputStream loadStreamWithFilePath(final String filePath);
}
