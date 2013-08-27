package ua.com.fielden.platform.gis.gps.actors;

import java.util.Date;

import ua.com.fielden.platform.utils.EntityUtils;

/**
 * A message type that contains request for last messages.
 */
public class LastMessagesRequest {
    private final Date afterDate;
    private final boolean onlyOne;

    public LastMessagesRequest(final Date afterDate) {
	this.afterDate = afterDate;
	this.onlyOne = EntityUtils.equalsEx(/*INFINITY_LEFT*/null, afterDate);
    }

    public Date getAfterDate() {
	return afterDate;
    }

    public boolean isOnlyOne() {
	return onlyOne;
    }
}
