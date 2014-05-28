package hotelmania.group4.platform;

import com.google.inject.Inject;
import hotelmania.group4.HotelManiaAgent;
import hotelmania.group4.client.ClientAgent;
import hotelmania.group4.guice.GuiceConfigurer;
import hotelmania.group4.settings.Settings;
import hotelmania.group4.utils.OnEndOfSimulation;
import hotelmania.group4.utils.SubscribeToDayEvents;
import hotelmania.group4.utils.SubscribeToEndOfSimulation;
import hotelmania.group4.utils.Utils;
import hotelmania.ontology.NotificationDayEvent;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 25/05/14
 */
public class ClientSpawner extends HotelManiaAgent {

    @Inject
    Settings settings;

    long clientCounter = 1;

    boolean simulationIsOn = true;

    @Override protected void setupHotelManiaAgent () {

        GuiceConfigurer.getInjector().injectMembers(this);

        final SubscribeToDayEvents subscribeToDayEvents = new SubscribeToDayEvents(this, new OnDayEvent() {
            @Override public void onDayEvent (NotificationDayEvent notificationDayEvent) {
                if(settings.getSimulationDays() - notificationDayEvent.getDayEvent().getDay() >= 1) {
                    for (int i = 0; i < settings.getNumberOfNewClientsPerDay(); i++) {
                        if (simulationIsOn) {
                            String clientAgentName = getNewClientName();
                            Utils.runAgent(ClientSpawner.this, clientAgentName, ClientAgent.class);
                        }
                    }
                }
            }
        });
        subscribeToDayEvents.doSubscription();

        new SubscribeToEndOfSimulation(this, new OnEndOfSimulation() {
            @Override public void onEndOfSimulation () {
                simulationIsOn = false;
                subscribeToDayEvents.unsubscribe();
                takeDown();
            }
        });
    }

    private String getNewClientName () {
        String name = "Client-" + clientCounter;
        clientCounter++;
        return name;
    }

}
