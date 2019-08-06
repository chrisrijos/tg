package ua.com.fielden.platform.web.app;

import java.io.InputStream;

/**
 * The contract for loading resources by their URIs of filePaths.
 *
 * @author TG Team
 *
 */
public interface ISourceController {
    
    /**
     * Loads the text representation of the resource with the specified 'resourceURI'.
     * <p>
     * Please, note that the resources should be accessed through the '/resources' prefix or one of the prefixes for generated resources: '/app', '/master_ui' or '/centre_ui'.
     *
     * @param resourceURI
     *
     * @return
     */
    String loadSource(final String resourceURI);
    
    /**
     * Loads the text representation of the resource with the specified 'filePath'.
     *
     * @param resourceURI
     *
     * @return
     */
    String loadSourceWithFilePath(final String filePath);
    
    /**
     * Loads input stream representation of the resource with the specified 'filePath'.
     *
     * @param resourceURI
     *
     * @return
     */
    InputStream loadStreamWithFilePath(final String filePath);
    
}