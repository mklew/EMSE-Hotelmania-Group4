package hotelmania.group4.client;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import hotelmania.group4.HotelManiaAgent;
import hotelmania.group4.guice.GuiceConfigurer;
import hotelmania.group4.jobs.Job;
import hotelmania.group4.jobs.WorkerPool;
import hotelmania.ontology.BookingOffer;
import hotelmania.ontology.HotelInformation;
import hotelmania.ontology.Price;
import hotelmania.ontology.Stay;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 26/05/14
 */
public class AskHotelsForPrice {

    public interface OnPrices {
        void prices (Map<HotelInformation, Price> callback);
    }

    private final CountDownLatch countDownLatch;

    private final OnPrices onPrices;

    private final Set<HotelInformation> hotelInformation;

    private final HotelManiaAgent agent;

    private final Stay stay;

    private final long timeOutInSeconds;

    private Map<HotelInformation, Price> hotelToPrice = new ConcurrentHashMap<>();

    @Inject
    WorkerPool workerPool;

    public AskHotelsForPrice (final Set<HotelInformation> hotelInformation, long timeoutInSeconds,
                              HotelManiaAgent agent, Stay stay, OnPrices onPrices) {
        countDownLatch = new CountDownLatch(hotelInformation.size());
        this.hotelInformation = hotelInformation;
        this.onPrices = onPrices;
        this.agent = agent;
        this.stay = stay;
        this.timeOutInSeconds = timeoutInSeconds;
        GuiceConfigurer.getInjector().injectMembers(this);
    }

    public void askForPrices () {
        for (final HotelInformation info : hotelInformation) {
            final AskForPrice askForPrice = new AskForPrice(agent, info.getHotel().getHotelAgent(), stay, new AskForPrice.PriceOfferCb() {
                @Override public void gotOffer (BookingOffer bookingOffer) {
                    countDownLatch.countDown();
                    hotelToPrice.put(info, bookingOffer.getRoomPrice());
                }

                @Override public void refused () {
                    countDownLatch.countDown();
                }
            });
            agent.addBehaviour(askForPrice);
        }

        awaitForResults();
    }

    /**
     * This has to run in other thread because otherwise agent would be sleeping until hotels answer with prices
     */
    private void awaitForResults () {
        workerPool.submitJob(new Job() {
            @Override public void doJob () throws Exception {
                try {
                    countDownLatch.await(timeOutInSeconds, TimeUnit.SECONDS);
                    onPrices.prices(ImmutableMap.copyOf(hotelToPrice));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
