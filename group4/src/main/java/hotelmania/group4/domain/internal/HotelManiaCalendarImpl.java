package hotelmania.group4.domain.internal;

import hotelmania.group4.domain.HotelManiaCalendar;
import hotelmania.ontology.DayEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 11/05/14
 */
public class HotelManiaCalendarImpl implements HotelManiaCalendar {

    Logger logger = LoggerFactory.getLogger(getClass());

    private int dayNumber = 0;

    @Override public void dayPassed () {
        dayNumber = dayNumber + 1;
        logger.info("Day has passed. New day number is {}", dayNumber);
    }

    @Override public DayEvent today () {
        final DayEvent dayEvent = new DayEvent();
        dayEvent.setDay(dayNumber);
        return dayEvent;
    }
}
