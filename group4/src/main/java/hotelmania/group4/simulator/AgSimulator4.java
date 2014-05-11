package hotelmania.group4.simulator;

import com.google.inject.Inject;
import hotelmania.group4.HotelManiaAgent;
import hotelmania.group4.domain.HotelManiaCalendar;
import hotelmania.group4.guice.GuiceConfigurer;
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
import java.util.UUID;
import java.util.Vector;

/**
 * @author Alberth Montero <alberthm@gmail.com>
 * @since 5/6/14
 */
public class AgSimulator4 extends HotelManiaAgent {

    public static final int DEFAULT_TIME_FOR_DAY = 5000;

    Logger logger = LoggerFactory.getLogger(getClass());

    private SubscriptionResponder subscriptionResponder;

    @Inject
    HotelManiaCalendar calendar;

    @Override
    protected void setupHotelManiaAgent () {
        // Added for testing 2nd iteration Tests
        GuiceConfigurer.getInjector().injectMembers(this);
        System.out.println(getLocalName() + ": HAS ENTERED");

        logger.debug("setting up agent");
        try {
            // Creates its own description
            DFAgentDescription dfd = Utils.createAgentDescriptionWithNameAndType(this.getName(), SUBSCRIBE_TO_DAY_EVENT);
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

        MessageTemplate messageTemplate = Utils.messageTemplateConjunction(Arrays.asList(
                MessageTemplate.MatchLanguage(getCodec().getName()),
                MessageTemplate.MatchOntology(getOntology().getName()),
                MessageTemplate.MatchPerformative(ACLMessage.SUBSCRIBE),
                MessageTemplate.MatchProtocol(SUBSCRIBE_TO_DAY_EVENT)
        ));
        subscriptionResponder = new SubscriptionResponder(this, messageTemplate, new SubscriptionResponder.SubscriptionManager() {
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


        }) {
            @Override
            protected ACLMessage handleSubscription (
                    ACLMessage subscription) throws NotUnderstoodException, RefuseException {
                if (subscription.getConversationId() == null) {
                    final String conversationId = subscription.getSender().getName() + " " + UUID.randomUUID().toString();
                    subscription.setConversationId(conversationId);
                }
                final ACLMessage aclMessage = super.handleSubscription(subscription);
                if (aclMessage == null) {
                    final ACLMessage reply = subscription.createReply();
                    reply.setPerformative(ACLMessage.AGREE);
                    return reply;
                } else {
                    return aclMessage;
                }
            }
        };
        addBehaviour(subscriptionResponder);

        // for sending the day change - ? Implement
        addBehaviour(new TickerBehaviour(this, DEFAULT_TIME_FOR_DAY) {
            protected void onTick () {
                final NotificationDayEvent notificationDayEvent = new NotificationDayEvent();
                notificationDayEvent.setDayEvent(calendar.today());

                final Vector subscriptions = subscriptionResponder.getSubscriptions();
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

                calendar.dayPassed();
            }
        });
    }
}



