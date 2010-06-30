package org.krohm.wicket.component.calendar.fullcalendar;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.wicket.IRequestTarget;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.markup.html.form.LabeledWebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.WebRequestCycle;
import org.apache.wicket.util.template.TextTemplateHeaderContributor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author arnaud
 */
public class FullCalendarComponent extends LabeledWebMarkupContainer {

    private static final Logger logger = LoggerFactory.getLogger(FullCalendarComponent.class);
    // Constants
    private static final String ON_CLICK_EVENT = "eventClick";
    private static final String ON_DROP_EVENT = "eventDrop";
    private static final String EVENT_TYPE_KEY = "EVENT_TYPE";
    // Resources Folders
    private static final String RESOURCES_PATH = "resources";
    private static final String FC_PATH = RESOURCES_PATH + "/fullcalendar";
    private static final String JQUERY_PATH = FC_PATH + "/jquery";
    // JavaScript Libs
    private static final String FC_SCRIPT_NAME = FC_PATH + "/fullcalendar.min.js";
    private static final String JQUERY_SCRIPT_NAME = JQUERY_PATH + "/jquery.js";
    private static final String JQUERY_UI_SCRIPT_NAME = JQUERY_PATH + "/jquery-ui-custom.js";
    // Static CSSs
    private static final String FX_CSS_NAME = FC_PATH + "/fullcalendar.css";
    // Custom JavaScripts
    private static final String CUSTOM_JS_NAME = RESOURCES_PATH + "/custom.js";
    private static final String CUSTOM_JS_TEMPLATE_NAME = RESOURCES_PATH + "/customTemplate.tpl.js";
    // Utils
    private final Map<String, PseudoBehavior> pseudoBehaviors = getPseudoBehaviors();

    public FullCalendarComponent(MarkupContainer parent, String id) {
        super(id);
        parent.add(this);
        initJs();
    }

    public FullCalendarComponent(MarkupContainer parent, String id, IModel<?> model) {
        super(id, model);
        parent.add(this);
        initJs();
    }

    private final void initJs() {

        // Static JavaScripts
        add(JavascriptPackageResource.getHeaderContribution(FullCalendarComponent.class, JQUERY_SCRIPT_NAME));
        add(JavascriptPackageResource.getHeaderContribution(FullCalendarComponent.class, JQUERY_UI_SCRIPT_NAME));
        add(JavascriptPackageResource.getHeaderContribution(FullCalendarComponent.class, FC_SCRIPT_NAME));
        add(JavascriptPackageResource.getHeaderContribution(FullCalendarComponent.class, CUSTOM_JS_NAME));
        // Static CSSs
        add(CSSPackageResource.getHeaderContribution(FullCalendarComponent.class, FX_CSS_NAME));
        this.setOutputMarkupId(true);
        // Behaviour for Event Management (Drag / Drop / Click ...)
        AjaxEventBehavior eventManagerAjaxBehaviour = new AjaxEventBehavior("EventBehavior") {

            @Override
            protected void onEvent(AjaxRequestTarget art) {
                logger.info("Event Received");
                Map originalMap = ((WebRequestCycle) RequestCycle.get()).getRequest().getParameterMap();
                Map<String, String> convertedMap = Util.convertMap(originalMap);
                String eventType = convertedMap.get(EVENT_TYPE_KEY);
                PseudoBehavior currentPseudoBehavior = pseudoBehaviors.get(eventType);
                currentPseudoBehavior.execute(convertedMap);
            }
        };

        // Behavior for getting Event List
        AbstractDefaultAjaxBehavior eventListAjaxBehaviour = new AbstractDefaultAjaxBehavior() {

            @Override
            protected void respond(AjaxRequestTarget target) {
                logger.info("Getting Event List");
                // getArguments
                Map originalMap = ((WebRequestCycle) RequestCycle.get()).getRequest().getParameterMap();
                Map<String, String> convertedMap = Util.convertMap(originalMap);
                final Date startDate = new Date(1000 * Long.parseLong(convertedMap.get("start")));
                final Date endDate = new Date(1000 * Long.parseLong(convertedMap.get("end")));
                logger.info("Start Date :<" + startDate + ">. End Date :<" + endDate + ">.");
                if (logger.isDebugEnabled()) {
                    logger.debug(convertedMap.toString());
                }
                getRequestCycle().setRequestTarget(new IRequestTarget() {

                    @Override
                    public void respond(RequestCycle requestCycle) {
                        String eventJson = Util.getEventListJson(getEventList(startDate, endDate)).toString();
                        if (logger.isDebugEnabled()) {
                            logger.debug(eventJson);
                        }
                        requestCycle.getResponse().write(eventJson);
                    }

                    @Override
                    public void detach(RequestCycle rc) {
                    }
                });
            }
        };
        // Custom Beheviors
        add(eventManagerAjaxBehaviour);
        add(eventListAjaxBehaviour);
        // Template Map Generation
        Map<String, Object> originalTemplateMap = new HashMap<String, Object>();
        originalTemplateMap.put("markupId", getMarkupId());
        originalTemplateMap.put("eventBehaviourUrl", eventManagerAjaxBehaviour.getCallbackUrl(true).toString());
        originalTemplateMap.put("getListBehaviourUrl", eventListAjaxBehaviour.getCallbackUrl(true).toString());
        // Dynamic JavaScript
        add(TextTemplateHeaderContributor.forJavaScript(FullCalendarComponent.class,
                CUSTOM_JS_TEMPLATE_NAME, Util.getTemplateKeys(originalTemplateMap)));
    }

    /*
     * Overridable Methods
     */
    public List<EventBean> getEventList(Date startDate, Date endDate) {
        logger.debug("Executing getEventList default Method");
        return null;
    }

    public void onEventDrop(EventBean currentEventBean, Integer dayDelta, Integer minuteDelta,
            Boolean allDay) {
        logger.debug("Executing eventDrop default Method");
    }

    public void onEventClick(EventBean currentEventBean) {
        logger.debug("Executing eventClick default Method");

    }

    /*
     *  PseudoBehaviours
     */
    private final Map<String, PseudoBehavior> getPseudoBehaviors() {
        Map<String, PseudoBehavior> returnMap = new HashMap<String, PseudoBehavior>();
        returnMap.put(ON_DROP_EVENT, new EventDragPseudoBehavior());
        returnMap.put(ON_CLICK_EVENT, new EventClickPseudoBehavior());
        return returnMap;
    }

    private interface PseudoBehavior {

        public void execute(Map<String, String> parameters);
    }

    private class EventDragPseudoBehavior implements PseudoBehavior {

        private static final String DAY_DELTA_KEY = "dayDelta";
        private static final String MINUTE_DELTA_KEY = "minuteDelta";
        private static final String ALL_DAY_KEY = "allDay";
        private static final String REVERT_FUNC_KEY = "revertFunc";

        public void execute(Map<String, String> parameters) {
            logger.error("we are in the eventDrop PseudoBehavior");
            try {
                EventBean currentEventBean = EventFactory.getEventBean(parameters);
                Integer dayDelta = Integer.parseInt(parameters.get(DAY_DELTA_KEY));
                Integer minuteDelta = Integer.parseInt(parameters.get(MINUTE_DELTA_KEY));
                Boolean allDay = "true".equals(parameters.get(ALL_DAY_KEY));
                onEventDrop(currentEventBean, dayDelta, minuteDelta, allDay);
            } catch (Exception ex) {
                logger.error("Error on Drag / Drop Event", ex);
            }

        }
    }

    private class EventClickPseudoBehavior implements PseudoBehavior {

        public void execute(Map<String, String> parameters) {
            try {
                EventBean currentEventBean = EventFactory.getEventBean(parameters);
                onEventClick(currentEventBean);
            } catch (Exception ex) {
                logger.error("Error on Click Event", ex);
            }
        }
    }
}
