package hotelmania.group4.simulator;

import com.google.inject.Inject;
import hotelmania.group4.HotelManiaAgent;
import hotelmania.group4.domain.HotelManiaCalendar;
import hotelmania.group4.guice.GuiceConfigurer;
import hotelmania.group4.settings.Settings;
import hotelmania.group4.utils.EmseSubscriptionResponder;
import hotelmania.group4.utils.Utils;
import hotelmania.ontology.NotificationDayEvent;
import jade.content.lang.Codec;
import jade.content.onto.OntologyException;
import jade.core.AID;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.SubscriptionResponder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Vector;

/**
 * @author Alberth Montero <alberthm@gmail.com>
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 5/6/14
 */
public class AgSimulator4 extends HotelManiaAgent {

    Logger logger = LoggerFactory.getLogger(getClass());

    private SubscriptionResponder dayEventsNotificationsSubscriptionResponder;

    @Inject
    HotelManiaCalendar calendar;

    @Inject
    Settings settings;

    private SubscriptionResponder endOfSimulationSubscriptionResponder;

    @Override
    protected void setupHotelManiaAgent () {
        // Added for testing 2nd iteration Tests
        GuiceConfigurer.getInjector().injectMembers(this);
        System.out.println(getLocalName() + ": HAS ENTERED");

        logger.debug("setting up agent");
        try {
            // Creates its own description
            DFAgentDescription dfd = Utils.createAgentDescriptionWithNameAndType(this.getName(), SUBSCRIBE_TO_DAY_EVENT, END_SIMULATION);
            // Registers its description in the DF
            DFService.register(this, dfd);
            logger.info(getLocalName() + ": registered in the DF");

            // Added for testing 2nd iteration Tests
            System.out.println(getLocalName() + ": registered in the DF");
            //doWait(10000); // TEST!
            // Wait 10 seconds for subscriptions
            System.out.println(getLocalName() + ": Waiting subscriptions ...");

        } catch (FIPAException e) {
            e.printStackTrace();
        }

        dayEventsNotificationsSubscriptionResponder = createDayEventsNotificationsSubscriptionsResponder();
        addBehaviour(dayEventsNotificationsSubscriptionResponder);

        endOfSimulationSubscriptionResponder = createEndOfSimulationSubscriptionsResponder();
        addBehaviour(endOfSimulationSubscriptionResponder);

        addBehaviour(new TickerBehaviour(this, lengthOfTheDay()) {
            protected void onTick () {
                logger.debug("Simulator tick");
                calendar.dayPassed();
                if (simulationShouldEnd()) {
                    sendEndOfSimulationMessages();
                } else {
                    final NotificationDayEvent notificationDayEvent = new NotificationDayEvent();
                    notificationDayEvent.setDayEvent(calendar.today());
                    logger.debug("Notifying about day {}", notificationDayEvent.getDayEvent().getDay());
                    final Vector subscriptions = dayEventsNotificationsSubscriptionResponder.getSubscriptions();
                    for (Object obj : subscriptions) {
                        SubscriptionResponder.Subscription subscription = (SubscriptionResponder.Subscription) obj;
                        final ACLMessage reply = subscription.getMessage().createReply();
                        reply.setPerformative(ACLMessage.INFORM);

                        try {
                            getContentManager().fillContent(reply, notificationDayEvent);
                        } catch (Codec.CodecException | OntologyException e) {
                            e.printStackTrace();
                        }

                        subscription.notify(reply);
                    }
                }

            }
        });
    }

    private int lengthOfTheDay () {
        return settings.getDayLengthInSeconds() * 1000;
    }

    private void sendEndOfSimulationMessages () {
        final Vector subscriptions = endOfSimulationSubscriptionResponder.getSubscriptions();
        for (Object obj : subscriptions) {
            SubscriptionResponder.Subscription subscription = (SubscriptionResponder.Subscription) obj;
            final ACLMessage reply = subscription.getMessage().createReply();
            reply.setPerformative(ACLMessage.INFORM);
            reply.setProtocol(END_SIMULATION);
            subscription.notify(reply);
        }
    }

    private boolean simulationShouldEnd () {
        return calendar.today().getDay() > settings.getSimulationDays();
    }

    private EmseSubscriptionResponder createDayEventsNotificationsSubscriptionsResponder () {
        MessageTemplate messageTemplate = Utils.messageTemplateConjunction(Arrays.asList(
                MessageTemplate.MatchLanguage(getCodec().getName()),
                MessageTemplate.MatchOntology(getOntology().getName()),
                MessageTemplate.MatchPerformative(ACLMessage.SUBSCRIBE),
                MessageTemplate.MatchProtocol(SUBSCRIBE_TO_DAY_EVENT)
        ));
        return new EmseSubscriptionResponder(this, messageTemplate, new SubscriptionResponder.SubscriptionManager() {
            @Override public boolean register (
                    SubscriptionResponder.Subscription subscription) throws RefuseException, NotUnderstoodException {
                final AID sender = subscription.getMessage().getSender();
                logger.info("Agent {} subscribed to day events", sender);
                return true;  // return value is ignored by default implementation of SubscriptionResponder
            }

            @Override
            public boolean deregister (SubscriptionResponder.Subscription subscription) throws FailureException {
                final AID sender = subscription.getMessage().getSender();
                logger.info("Agent {} unsubscribed from day events", sender);
                return true;  // return value is ignored by default implementation of SubscriptionResponder
            }

        });
    }

    private EmseSubscriptionResponder createEndOfSimulationSubscriptionsResponder () {
        MessageTemplate messageTemplate = Utils.messageTemplateConjunction(Arrays.asList(
                MessageTemplate.MatchLanguage(getCodec().getName()),
                MessageTemplate.MatchOntology(getOntology().getName()),
                MessageTemplate.MatchPerformative(ACLMessage.SUBSCRIBE),
                MessageTemplate.MatchProtocol(END_SIMULATION)
        ));
        return new EmseSubscriptionResponder(this, messageTemplate, new SubscriptionResponder.SubscriptionManager() {
            @Override public boolean register (
                    SubscriptionResponder.Subscription subscription) throws RefuseException, NotUnderstoodException {
                final AID sender = subscription.getMessage().getSender();
                logger.info("Agent {} subscribed to end of simulation", sender);
                return true;  // return value is ignored by default implementation of SubscriptionResponder
            }

            @Override
            public boolean deregister (SubscriptionResponder.Subscription subscription) throws FailureException {
                final AID sender = subscription.getMessage().getSender();
                logger.info("Agent {} unsubscribed from end of simulation", sender);
                return true;  // return value is ignored by default implementation of SubscriptionResponder
            }
        });
    }

}



