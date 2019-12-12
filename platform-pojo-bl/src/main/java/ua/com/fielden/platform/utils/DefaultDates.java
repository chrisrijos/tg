package ua.com.fielden.platform.utils;

import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.joda.time.DateTimeZone.forID;
import static org.joda.time.DateTimeZone.getDefault;
import static org.joda.time.format.DateTimeFormat.forPattern;
import static ua.com.fielden.platform.utils.EntityUtils.dateWithoutTimeFormat;

import java.util.Date;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * Default implementation of {@link IDates} contract containing thread-local time-zone for each client-originating server resources.
 * This time-zone is to be used when converting timeline moments to time-zone 
 * 
 * @author TG Team
 *
 */
public class DefaultDates implements IDates {
    private static final String SERVER_TIME_ZONE_APPLIED = "Server time-zone will be used.";
    private final Logger logger = Logger.getLogger(getClass());
    private final boolean independentTimeZone;
    private final ThreadLocal<DateTimeZone> clientTimeZone = new ThreadLocal<>();
    
    @Inject
    public DefaultDates(final @Named("independent.time.zone") boolean independentTimeZone) {
        this.independentTimeZone = independentTimeZone;
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
            } catch (final IllegalArgumentException ex) {
                logger.error(format("Unknown client time-zone [%s]. %s", timeZoneId, SERVER_TIME_ZONE_APPLIED), ex);
            }
        }
        // else {
        //     logger.debug(format("Empty client time-zone string is returned from client application. %s", SERVER_TIME_ZONE_APPLIED));
        // }
    }
    
    @Override
    public DateTime now() {
        if (clientTimeZone.get() != null) {
            final DateTime nowInClientTimeZone = new DateTime(clientTimeZone.get());
            return independentTimeZone ? nowInClientTimeZone.withZoneRetainFields(getDefault()) : nowInClientTimeZone;
        } else {
            return new DateTime(); // now in server time-zone; used as a fallback where no time-zone was used.
        }
    }
    
    @Override
    public DateTime zoned(final Date date) {
        return new DateTime(date, timeZone());
    }
    
    @Override
    public String toString(final DateTime dateTime) {
        return forPattern(dateWithoutTimeFormat + " hh:mm a").print(dateTime);
    }
    
    @Override
    public String toString(final Date date) {
        return toString(zoned(date));
    }
    
}