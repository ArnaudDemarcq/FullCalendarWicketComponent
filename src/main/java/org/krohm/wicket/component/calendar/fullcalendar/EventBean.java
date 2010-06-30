package org.krohm.wicket.component.calendar.fullcalendar;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author arnaud
 */
public class EventBean {

    // public constants
    public static final String EVENT_CLASS_KEY = "EVENT_CLASS_NAME";
    private static final Logger logger = LoggerFactory.getLogger(FullCalendarComponent.class);
    // Constants
    private static final String EVENT_ROOT = "Event_";
    private static final String EVENT_ID_KEY = EVENT_ROOT + "id";
    private static final String EVENT_URL_KEY = EVENT_ROOT + "url";
    private static final String EVENT_TITLE_KEY = EVENT_ROOT + "title";
    private static final String EVENT_ALLDAY_KEY = EVENT_ROOT + "allDay";
    private static final String EVENT_START_KEY = EVENT_ROOT + "GEN_start";
    private static final String EVENT_END_KEY = EVENT_ROOT + "GEN_end";
    // variables
    private String id = null;
    private String title = null;
    private Boolean allDay = null;
    private Date start = null;
    private Date end = null;
    private String url = null;

    public EventBean() {
    }

    public Boolean getAllDay() {
        return allDay;
    }

    public void setAllDay(Boolean allDay) {
        this.allDay = allDay;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /*
     * Population from DataMap
     */
    public void populateFromMap(Map<String, String> dataMap) {
        // A few Input Fields Logging
        if (logger.isDebugEnabled()) {
            for (String key : dataMap.keySet()) {
                String value = dataMap.get(key);
                logger.debug("Found key :<" + key + "> With value :<" + value + ">");
            }
        }
        // Mandatory Fields : start and title
        this.setTitle(dataMap.get(EVENT_TITLE_KEY));
        String startString = dataMap.get(EVENT_START_KEY);
        Long startLong = Long.parseLong(startString);
        setStart(new Date(startLong));
        // Optional Fields
        this.setId(dataMap.get(EVENT_ID_KEY));
        this.setUrl(dataMap.get(EVENT_URL_KEY));
        if (dataMap.get(EVENT_ALLDAY_KEY) != null) {
            this.setAllDay(dataMap.get(EVENT_ALLDAY_KEY).toLowerCase().equals("true"));
        }
        if (dataMap.get(EVENT_END_KEY) != null) {
            this.setEnd(new Date(Long.parseLong(dataMap.get(EVENT_END_KEY))));
        }
        logger.error("=>>>>>>>>>>>>>>>" + getStart());
    }
    /*
     * Json Serialization
     */

    protected Map<String, Object> jsonData() {
        Map<String, Object> returnMap = new HashMap<String, Object>();
        // Mandatory Data
        returnMap.put("title", getTitle());
        returnMap.put("start", (getStart().getTime() / 1000));
        // Optional Data
        if (id != null) {
            returnMap.put("id", getId());
        }
        if (allDay != null) {
            returnMap.put("allDay", getAllDay().booleanValue());
        }
        if (end != null) {
            returnMap.put("end", (getEnd().getTime() / 1000));
        }
        if (url != null) {
            returnMap.put("url", getUrl());
        }
        return returnMap;
    }

    @Override
    public final String toString() {
        return jsonData().toString();
    }
}
