package hotelmania.group4.domain;

import hotelmania.ontology.DayEvent;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 11/05/14
 */
public interface HotelManiaCalendar {

    void dayPassed ();

    DayEvent today ();
}
