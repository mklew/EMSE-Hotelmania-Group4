package hotelmania.group4.client;

import com.google.inject.Inject;
import hotelmania.group4.HotelManiaAgent;
import hotelmania.group4.domain.HotelManiaCalendar;
import hotelmania.group4.domain.client.Client;
import hotelmania.group4.domain.client.ClientGenerator;
import hotelmania.group4.guice.GuiceConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 18/05/14
 */
public class ClientAgent extends HotelManiaAgent {

    Logger logger = LoggerFactory.getLogger(getClass());

    Client client;

    @Inject
    ClientGenerator clientGenerator;

    @Inject
    HotelManiaCalendar calendar;

    @Override protected void setupHotelManiaAgent () {
        GuiceConfigurer.getInjector().injectMembers(this);

        client = clientGenerator.generateClient(calendar.today().getDay());

        // TODO implement client agent behaviours
    }
}
