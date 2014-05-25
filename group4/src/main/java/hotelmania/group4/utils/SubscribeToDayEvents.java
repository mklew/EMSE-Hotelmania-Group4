package hotelmania.group4.utils;

import hotelmania.group4.HotelManiaAgent;
import hotelmania.group4.HotelManiaAgentNames;
import hotelmania.group4.platform.OnDayEvent;
import hotelmania.ontology.NotificationDayEvent;
import jade.content.ContentElement;

public class SubscribeToDayEvents extends SubscribeTo {
    private OnDayEvent onDayEvent;

    public SubscribeToDayEvents (HotelManiaAgent agent, final OnDayEvent onDayEvent) {
        super(HotelManiaAgentNames.SUBSCRIBE_TO_DAY_EVENT, agent);
        this.onDayEvent = onDayEvent;
    }

    @Override protected void handleContent (ContentElement content) {
        NotificationDayEvent notificationDayEvent = (NotificationDayEvent) content;
        onDayEvent.onDayEvent(notificationDayEvent);
    }

}