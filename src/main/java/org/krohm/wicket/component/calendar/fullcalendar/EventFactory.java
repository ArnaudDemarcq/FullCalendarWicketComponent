/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.krohm.wicket.component.calendar.fullcalendar;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author arnaud
 */
public final class EventFactory {

    private static final String DEFAULT_EVENT_CLASS = EventBean.class.getName();
    private static final Logger logger = LoggerFactory.getLogger(FullCalendarComponent.class);

    public final static EventBean getEventBean(String className, Map<String, String> parameters)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException {

        Class eventBeanClass = Class.forName(className);
        EventBean currentBean = (EventBean) eventBeanClass.newInstance();
        currentBean.populateFromMap(parameters);
        return currentBean;
    }

    public final static EventBean getEventBean(Map<String, String> parameters)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        String className = parameters.get(EventBean.EVENT_CLASS_KEY);
        if (className == null) {
            className = DEFAULT_EVENT_CLASS;
        }
        return getEventBean(className, parameters);
    }
}
