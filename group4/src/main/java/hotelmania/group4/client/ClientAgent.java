package hotelmania.group4.client;

import com.google.inject.Inject;
import hotelmania.group4.HotelManiaAgent;
import hotelmania.group4.domain.HotelManiaCalendar;
import hotelmania.group4.domain.HotelRepositoryService;
import hotelmania.group4.domain.client.Client;
import hotelmania.group4.domain.client.ClientGenerator;
import hotelmania.group4.guice.GuiceConfigurer;
import hotelmania.group4.platform.OnDayEvent;
import hotelmania.group4.settings.Settings;
import hotelmania.group4.utils.OnEndOfSimulation;
import hotelmania.group4.utils.SubscribeToDayEvents;
import hotelmania.group4.utils.SubscribeToEndOfSimulation;
import hotelmania.ontology.DayEvent;
import hotelmania.ontology.HotelInformation;
import hotelmania.ontology.NotificationDayEvent;
import hotelmania.ontology.Price;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * First client is created after day 1 notification has been received.
 *
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

    @Inject
    HotelRepositoryService hotelRepositoryService;

    @Inject
    Settings settings;

    boolean isStayingInHotel = false;

    private Map<HotelInformation, Price> hotelToPrice = new HashMap<>();

    @Override protected void setupHotelManiaAgent () {
        GuiceConfigurer.getInjector().injectMembers(this);

        client = clientGenerator.generateClient(calendar.today().getDay());

        final SubscribeToDayEvents subscribeToDayEvents = new SubscribeToDayEvents(this, new OnDayEvent() {
            @Override public void onDayEvent (NotificationDayEvent notificationDayEvent) {
                doClientStuff(notificationDayEvent.getDayEvent());
            }
        });

        subscribeToDayEvents.doSubscription();

        final SubscribeToEndOfSimulation subscribeToEndOfSimulation = new SubscribeToEndOfSimulation(this, new OnEndOfSimulation() {
            @Override public void onEndOfSimulation () {
                // TODO do something during end of simulation
            }
        });

        subscribeToEndOfSimulation.doSubscription();

        performStuffToday();
    }

    private void performStuffToday () {
        doClientStuff(calendar.today());
    }

    /**
     * TODO client logic. so far it only asks for price everyday
     * @param dayEvent
     */
    private void doClientStuff (DayEvent dayEvent) {
        logger.info(getName() + " doing client stuff during day {}", dayEvent.getDay());

        final Set<HotelInformation> hotelInformation = hotelRepositoryService.getHotelInformation();
        final AskHotelsForPrice.OnPrices onPrices = new AskHotelsForPrice.OnPrices() {
            @Override public void prices (Map<HotelInformation, Price> callback) {
                logger.debug("Got prices {}", callback.toString());
            }
        };
        final AskHotelsForPrice askHotelsForPrice = new AskHotelsForPrice(hotelInformation, getTimeout(), this, client.getStay(), onPrices);
        askHotelsForPrice.askForPrices();


        // TODO actual client logic
        if (!isStayingInHotel) {
            if (client.hasToBookToday(dayEvent.getDay())) {


            }
        } else {

        }

    }

    public long getTimeout () {
        return settings.getDayLengthInSeconds() / 2;
    }
}
