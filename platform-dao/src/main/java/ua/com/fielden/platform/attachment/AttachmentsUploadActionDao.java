package ua.com.fielden.platform.attachment;

import static java.lang.String.format;
import static ua.com.fielden.platform.error.Result.failure;
import static ua.com.fielden.platform.reflection.TitlesDescsGetter.getEntityTitleAndDesc;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

import ua.com.fielden.platform.dao.CommonEntityDao;
import ua.com.fielden.platform.dao.annotations.SessionRequired;
import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.annotation.EntityType;
import ua.com.fielden.platform.entity.query.IFilter;

@EntityType(AttachmentsUploadAction.class)
public class AttachmentsUploadActionDao extends CommonEntityDao<AttachmentsUploadAction> implements IAttachmentsUploadAction {

    private static final Logger LOGGER = Logger.getLogger(AttachmentsUploadActionDao.class);
    
    @Inject
    protected AttachmentsUploadActionDao(final IFilter filter) {
        super(filter);
    }

    @Override
    @SessionRequired
    public AttachmentsUploadAction save(final AttachmentsUploadAction action) {
        // if there are attachments and master entity then build their associations
        if (action.getMasterEntity() != null && !action.getAttachments().isEmpty()) {
            final Class<? extends AbstractEntity<?>> entityType = action.getMasterEntity().getType();
            if (co$(action.getMasterEntity().getType()) instanceof ICanAttach) {
                final ICanAttach co = (ICanAttach) co$(entityType);
                action.getAttachments().stream().forEach(att -> co.attach(att, action.getMasterEntity()));
            } else {
                throw failure(format("Companion for %s cannot attach attachments.", getEntityTitleAndDesc(entityType).getKey()));
            }
            
        } else { // otherwise do nothing...
            LOGGER.debug("Either master entity or attachments are missing.");
        }
        
        return action;
    }
    
}
