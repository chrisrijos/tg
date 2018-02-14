package ua.com.fielden.platform.web.resources.webui;

import static ua.com.fielden.platform.web.utils.WebUiResourceUtils.handleUndesiredExceptions;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Set;
import java.util.function.Function;

import org.apache.commons.lang.StringUtils;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;
import org.restlet.routing.Router;

import ua.com.fielden.platform.dao.IEntityDao;
import ua.com.fielden.platform.entity.AbstractEntityWithInputStream;
import ua.com.fielden.platform.entity.factory.EntityFactory;
import ua.com.fielden.platform.rx.observables.ProcessingProgressSubject;
import ua.com.fielden.platform.web.resources.RestServerUtil;
import ua.com.fielden.platform.web.rx.eventsources.ProcessingProgressEventSource;
import ua.com.fielden.platform.web.sse.resources.EventSourcingResourceFactory;

/**
 * This resource should be used for uploading files to be processed with the specified functional entity.
 * 
 * Unlike {@link AttachmentTypeResource} it does not save or associated the uploaded file with any entity. Instead it passes that file into
 * 
 * @author TG Team
 * 
 */
public class FileProcessingResource<T extends AbstractEntityWithInputStream<?>> extends ServerResource {

    private final IEntityDao<T> companion;
    private final EntityFactory factory;
    private final Function<EntityFactory, T> entityCreator;
    private final RestServerUtil restUtil;
    private final long sizeLimit;
    private final Set<MediaType> types;
    private final Router router;
    private final String jobUid;
    private final String origFileName;
    private final Date fileLastModified;

    public FileProcessingResource(
            final Router router, 
            final IEntityDao<T> companion, 
            final EntityFactory factory, 
            final Function<EntityFactory, T> entityCreator, 
            final RestServerUtil restUtil, 
            final long fileSizeLimit, 
            final Set<MediaType> types, 
            final Context context, 
            final Request request, 
            final Response response) {
        init(context, request, response);
        this.router = router;
        this.companion = companion;
        this.factory = factory;
        this.entityCreator = entityCreator;
        this.restUtil = restUtil;
        this.sizeLimit = fileSizeLimit;
        this.types = types;
        
        this.jobUid = request.getHeaders().getFirstValue("jobUid");
        if (StringUtils.isEmpty(jobUid)) {
            throw new IllegalArgumentException("jobUid is required");
        }
        
        this.origFileName = request.getHeaders().getFirstValue("origFileName");
        if (StringUtils.isEmpty(origFileName)) {
            throw new IllegalArgumentException("origFileName is required");
        }
        
        final long lastModified = Long.parseLong(request.getHeaders().getFirstValue("lastModified"));
        this.fileLastModified = new Date(lastModified);
    }

    /**
     * Receives a file from a client.
     *
     * @throws IOException
     */
    @Put
    public Representation receiveFile(final Representation entity) throws IOException {
        final Representation response;
        if (entity == null) {
            getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
            return restUtil.errorJSONRepresentation("There is nothing to process");
        } else if (!isMediaTypeSupported(entity.getMediaType())) {
            getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
            return restUtil.errorJSONRepresentation("Unexpected media type.");
        } else if (entity.getSize() == -1) {
            getResponse().setStatus(Status.CLIENT_ERROR_LENGTH_REQUIRED);
            return restUtil.errorJSONRepresentation("File size is required.");
        } else if (entity.getSize() > sizeLimit) {
            getResponse().setStatus(Status.CLIENT_ERROR_REQUEST_ENTITY_TOO_LARGE);
            return restUtil.errorJSONRepresentation("File is too large.");
        } else {
            final InputStream stream = entity.getStream();
            response = handleUndesiredExceptions(getResponse(), () -> tryToProcess(stream, entity.getMediaType()), restUtil);
        }

        return response;
    }

    private boolean isMediaTypeSupported(MediaType mediaType) {
        // simply checking types.contains(mediaType) may not be sufficient as there could be something like IMAGE/*
        return types.contains(mediaType);
    }

    /**
     * Registers an event sourcing resource that sends file processing progress back to the client.
     * Instantiates an entity that is responsible for file processing and executes it (call method <code>save</code>).
     *
     * @param stream -- a stream that represents a file to be processed.
     * @return
     */
    private Representation tryToProcess(final InputStream stream, final MediaType mime) {
        final ProcessingProgressSubject subject = new ProcessingProgressSubject();
        final EventSourcingResourceFactory eventSource = new EventSourcingResourceFactory(new ProcessingProgressEventSource(subject));
        final String baseUri = getRequest().getResourceRef().getPath(true);
        router.attach(baseUri + "/" + jobUid, eventSource);
        
        try {
            final T entity = entityCreator.apply(factory);
            entity.setOrigFileName(origFileName);
            entity.setLastModified(fileLastModified);
            entity.setInputStream(stream);
            entity.setEventSourceSubject(subject);
            entity.setMime(mime.getName());

            final T applied = companion.save(entity);
            return restUtil.singleJSONRepresentation(applied);
        } finally {
            router.detach(eventSource);
        }
    }

}
