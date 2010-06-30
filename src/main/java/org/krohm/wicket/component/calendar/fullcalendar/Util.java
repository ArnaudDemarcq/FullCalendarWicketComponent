/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.krohm.wicket.component.calendar.fullcalendar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.collections.MiniMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author arnaud
 */
public class Util {

    private static final Logger logger = LoggerFactory.getLogger(Util.class);
    /*
     * Static public Utils Methods
     */

    protected final static IModel<Map<String, Object>> getTemplateKeys(final Map<String, Object> originalMap) {


        IModel<Map<String, Object>> variablesModel = new AbstractReadOnlyModel<Map<String, Object>>() {

            private Map<String, Object> variables;
            private static final int MAX_ENTRIES = 32;

            public Map<String, Object> getObject() {
                if (variables == null) {
                    this.variables = new MiniMap<String, Object>(MAX_ENTRIES);
                    for (String key : originalMap.keySet()) {
                        variables.put(key, originalMap.get(key));
                    }
                }
                return variables;
            }
        };
        return variablesModel;
    }

    // to convert data from HTTP Request to usable data
    protected static final Map<String, String> convertMap(Map<Object, Object> originalMap) {
        Map<String, String> returnMap = new HashMap<String, String>();
        for (Object objectKey : originalMap.keySet()) {
            Object[] objectValue = (Object[]) originalMap.get(objectKey);
            if (logger.isDebugEnabled()) {
                logger.debug("Found key :<" + objectKey + "> With value :<" + objectValue[0] + ">");
            }
            String key = (String) objectKey;
            String value = (String) objectValue[0];
            returnMap.put(key, value);
        }
        return returnMap;
    }

    /*
     * Private Methods
     */

    protected final static JSONArray getEventListJson(List<EventBean> eventList) {
        JSONArray jsonArray = new JSONArray();
        for (EventBean currentEvent : eventList) {
            jsonArray.put(JSONObject.fromMap(currentEvent.jsonData()));
        }
        return jsonArray;
    }/**/

}
