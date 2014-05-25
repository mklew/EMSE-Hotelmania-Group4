package hotelmania.group4.platform;

import hotelmania.ontology.NotificationDayEvent;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 25/05/14
 */
public interface OnDayEvent {
    void onDayEvent(NotificationDayEvent notificationDayEvent);
}
