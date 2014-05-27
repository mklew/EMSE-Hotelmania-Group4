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
import hotelmania.ontology.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

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

    boolean bookedRoom = false;

    private Map<HotelInformation, Price> hotelToPrice = new HashMap<>();

    private class BestOffer {
        private final HotelInformation hotelInformation;

        private final Price price;

        private BestOffer (HotelInformation hotelInformation, Price price) {
            this.hotelInformation = hotelInformation;
            this.price = price;
        }

        private HotelInformation getHotelInformation () {
            return hotelInformation;
        }

        private Price getPrice () {
            return price;
        }
    }

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
     * TODO client logic. so far it books first offer it gets
     *
     * @param dayEvent
     */
    private void doClientStuff (DayEvent dayEvent) {
        logger.info(getName() + " doing client stuff during day {}", dayEvent.getDay());

        if (bookedRoom) {
            // TODO wait for checkOut day and then ask for staff during days and then give rating
        } else {

            final Set<HotelInformation> hotelInformation = hotelRepositoryService.getHotelInformation();
            final AskHotelsForPrice.OnPrices onPrices = new AskHotelsForPrice.OnPrices() {
                @Override public void prices (Map<HotelInformation, Price> prices) {
                    logger.debug("Got prices {}", prices.toString());

                    List<BestOffer> bestOffers = findBestOffers(prices);
                    if (bestOffers.size() > 0) {
                        final BestOffer next = bestOffers.iterator().next();
                        BookRoom bookRoom = new BookRoom();
                        bookRoom.setPrice(next.getPrice());
                        bookRoom.setStay(client.getStay());
                        final DoBookRoom doBookRoom = new DoBookRoom(ClientAgent.this, next.getHotelInformation().getHotel().getHotelAgent(), bookRoom, new DoBookRoom.BookResult() {
                            @Override public void success () {
                                bookedRoom = true;
                            }

                            @Override public void fail () {
                                // TODO try second best or do something else depends on the day
                            }
                        });
                        addBehaviour(doBookRoom);
                    }
                }
            };
            final AskHotelsForPrice askHotelsForPrice = new AskHotelsForPrice(hotelInformation, getTimeout(), this, client.getStay(), onPrices);
            askHotelsForPrice.askForPrices();

        }
    }

    private List<BestOffer> findBestOffers (Map<HotelInformation, Price> prices) {
        // TODO implement it according to spec
        List<BestOffer> bestOffers = new ArrayList<>();

        final Map.Entry<HotelInformation, Price> next = prices.entrySet().iterator().next();

        final HotelInformation key = next.getKey();
        final Price value = next.getValue();

        bestOffers.add(new BestOffer(key, value));

        return bestOffers;
    }

    public long getTimeout () {
        return settings.getDayLengthInSeconds() / 2;
    }
}
