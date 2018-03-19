package ua.com.fielden.platform.web.resources.webui;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;

import com.google.common.base.Charsets;

import ua.com.fielden.platform.utils.ResourceLoader;
import ua.com.fielden.platform.web.app.ISourceController;
import ua.com.fielden.platform.web.interfaces.IDeviceProvider;
import ua.com.fielden.platform.web.resources.RestServerUtil;

/**
 * Web server resource that searches for file resource among resource paths and returns it to client.
 *
 * @author TG Team
 *
 */
public class FileResource extends DeviceProfileDifferentiatorResource {
    private final Logger logger = Logger.getLogger(getClass());
    private final List<String> resourcePaths;
    private final ISourceController sourceController;
    
    /**
     * Creates an instance of {@link FileResource} with custom resource paths.
     *
     * @param resourcePaths
     * @param context
     * @param request
     * @param response
     */
    public FileResource(final ISourceController sourceController, final List<String> resourcePaths, final IDeviceProvider deviceProvider, final Context context, final Request request, final Response response) {
        super(context, request, response, deviceProvider);
        this.resourcePaths = resourcePaths;
        this.sourceController = sourceController;
    }

    /**
     * Invoked on GET request from client.
     */
    @Override
    protected Representation get() {
        final String originalPath = getReference().getRemainingPart();
        final String extension = getReference().getExtensions();
        final MediaType mediaType = determineMediaType(extension);

        final String filePath = generateFileName(resourcePaths, originalPath);
        if (StringUtils.isEmpty(filePath)) {
            new FileNotFoundException("The requested resource (" + originalPath + " + " + extension + ") wasn't found.").printStackTrace();
            return null;
        } else {
            if (MediaType.TEXT_HTML.equals(mediaType)) {
                final String source = sourceController.loadSourceWithFilePath(filePath, device());
                if (source != null) {
                    return RestServerUtil.encodedRepresentation(new ByteArrayInputStream(source.getBytes(Charsets.UTF_8)), mediaType);
                } else {
                    return null;
                }
            } else {
                final InputStream stream = sourceController.loadStreamWithFilePath(filePath);
                if (stream != null) {
                    final Representation encodedRepresentation = RestServerUtil.encodedRepresentation(stream, mediaType);
                    logger.debug(String.format("File resource [%s] generated.", originalPath));
                    return encodedRepresentation;
                } else {
                    return null;
                }
            }
        }
    }

    /**
     * Searches for the file resource among resource paths starting from the last one path and generates full file path by concatenating resource path and relative file path.
     *
     * @param filePath
     *            - the relative file path for which full file path must be generated.
     * @return
     */
    public static String generateFileName(final List<String> resourcePaths, final String path) {
        // this is a preventive stuff: if the server receives additional link parameters -- JUST IGNORE THEM. Was used to run
        // appropriately Mocha / Chai tests for Polymer web components. See http://localhost:8091/resources/polymer/runner.html for results.
        final String filePath = path.contains("?") ? path.substring(0, path.indexOf('?')) : path;

        for (int pathIndex = 0; pathIndex < resourcePaths.size(); pathIndex++) {
            final String prepender = resourcePaths.get(pathIndex);
            if (ResourceLoader.exist(prepender + filePath)) {
                return prepender + filePath;
            }
        }
        return null;
    }

    /**
     * Determines the media type of the file to return to the client. The determination process is based on file extension.
     *
     * @param extension
     *            - the file extension that is used to determine media type.
     * @return
     */
    private static MediaType determineMediaType(final String extension) {
        switch (extension) {
        case "png":
            return MediaType.IMAGE_PNG;
        case "js":
        case "min.js":
        case "json":
            return MediaType.TEXT_JAVASCRIPT;
        case "html":
            return MediaType.TEXT_HTML;
        case "css":
            return MediaType.TEXT_CSS;
        case "svg":
            return MediaType.IMAGE_SVG;
        default:
            return MediaType.ALL;
        }
    }

    //	private static String compress(final String str) throws IOException {
    //		if (str == null || str.length() == 0) {
    //			return str;
    //		}
    //		ByteArrayOutputStream out = new ByteArrayOutputStream();
    //		GZIPOutputStream gzip = new GZIPOutputStream(out);
    //		gzip.write(str.getBytes());
    //		gzip.close();
    //		String outStr = out.toString("UTF-8");
    //		return outStr;
    //	}
}