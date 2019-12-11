package ua.com.fielden.platform.utils;

import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.joda.time.DateTimeZone.forID;
import static org.joda.time.DateTimeZone.getDefault;
import static org.joda.time.format.DateTimeFormat.forPattern;
import static ua.com.fielden.platform.utils.EntityUtils.dateWithoutTimeFormat;

import java.util.Date;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class DefaultUniversalConstants implements IUniversalConstants {
    private static final String SERVER_TIME_ZONE_APPLIED = "Server time-zone will be used.";
    private final Logger logger = Logger.getLogger(getClass());
    private final String appName;
    private final String smtpServer;
    private final String fromEmailAddress;
    private final boolean independentTimeZone;
    private final ThreadLocal<DateTimeZone> clientTimeZone = new ThreadLocal<>();
    
    @Inject
    public DefaultUniversalConstants(
            final @Named("app.name") String appName,
            final @Named("email.smtp") String smtpServer,
            final @Named("email.fromAddress") String fromEmailAddress,
            final @Named("independent.time.zone") boolean independentTimeZone) {
        this.appName = appName;
        this.smtpServer = smtpServer;
        this.fromEmailAddress = fromEmailAddress;
        this.independentTimeZone = independentTimeZone;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Locale locale() {
        return Locale.getDefault();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String smtpServer() {
        return smtpServer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String fromEmailAddress() {
        return fromEmailAddress;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String appName() {
        return appName;
    }
    
    @Override
    public DateTimeZone timeZone() {
        return independentTimeZone ? getDefault() : clientTimeZone.get() == null ? getDefault() : clientTimeZone.get();
    }
    
    public void setClientTimeZone(final String timeZoneString) {
        if (!isEmpty(timeZoneString)) {
            // in case where multiple 'Time-Zone' headers have been returned, just use the first one; this is for additional safety if official 'Time-Zone' header will appear in future
            final String timeZoneId = timeZoneString.contains(",") ? timeZoneString.split(",")[0] : timeZoneString.trim(); // ',' is not a special character in reg expressions, no need to escape it
            try {
                clientTimeZone.set(forID(timeZoneId));
                logger.info("timeZoneId = " + timeZoneId);
            } catch (final IllegalArgumentException ex) {
                logger.error(format("Unknown client time-zone [%s]. %s", timeZoneId, SERVER_TIME_ZONE_APPLIED), ex);
            }
        } else {
            logger.debug(format("Empty client time-zone string is returned from client application. %s", SERVER_TIME_ZONE_APPLIED));
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public DateTime now() {
        if (clientTimeZone.get() != null) {
            final DateTime nowInClientTimeZone = new DateTime(clientTimeZone.get());
            return independentTimeZone ? nowInClientTimeZone.withZoneRetainFields(getDefault()) : nowInClientTimeZone;
        } else {
//            final Exception ex = new Exception();
//            if (Stream.of(ex.getStackTrace()).allMatch(ste -> 
//                !ste.getClassName().equals("ua.com.fielden.platform.web.resources.webui.LoginResource")
//                && !ste.getClassName().equals("ua.com.fielden.platform.web.security.AbstractWebResourceGuard")
//                && !ste.getClassName().equals("ua.com.fielden.platform.web.resources.webui.AppIndexResource")
//                && !ste.getClassName().equals("ua.com.fielden.platform.web.resources.webui.CentreComponentResource")
//                && !ste.getClassName().equals("fielden.web.AccessAuditEventProcessor"))) {
//                logger.warn("now() was used, but no client-specific time-zone assigned. Explore the stack trace of this call and consider adding of 'Time-Zone' header to request.", new Exception());
//            }
            return new DateTime(); // now in server time-zone; used as a fallback where no time-zone was used.
        }
    }
    
    @Override
    public DateTime dt(final Date date) {
        return new DateTime(date, timeZone());
    }
    
    @Override
    public String toString(final DateTime dateTime) {
        return forPattern(dateWithoutTimeFormat + " hh:mm a").print(dateTime);
    }
    
    @Override
    public String toString(final Date date) {
        return toString(dt(date));
    }
    
}