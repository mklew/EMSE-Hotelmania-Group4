package emse.abs.hotelmania.group4;

import com.google.common.base.Function;
import emse.abs.hotelmania.behaviours.EmseSimpleBehaviour;
import emse.abs.hotelmania.behaviours.MessageStatus;
import emse.abs.hotelmania.ontology.Hotel;
import emse.abs.hotelmania.ontology.RegistrationRequest;
import jade.content.lang.Codec;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 20/04/14
 */
public class AgHotel4 extends HotelManiaAgent {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected void setupHotelManiaAgent () {

        logger.debug("setting up agent");

        addBehaviour(new SearchForHotelMania(this, new Function<DFAgentDescription[], Object>() {
            @Override public Object apply (DFAgentDescription[] dfAgentDescriptions) {
                if (dfAgentDescriptions.length > 1) {
                    logger.error("More than 1 hotel mania agents found");
                } else {
                    final DFAgentDescription dfAgentDescription = dfAgentDescriptions[0];
                    final AID hotelmania = dfAgentDescription.getName();

                    ACLMessage msg = createMessage(hotelmania, ACLMessage.REQUEST);

                    RegistrationRequest registrationRequest = new RegistrationRequest();
                    try {
                        final Hotel hotel = new Hotel();
                        hotel.setHotel_name("hotel4"); // TODO extract that
                        registrationRequest.setHotel(hotel);

                        // As it is an action and the encoding language the SL, it must be wrapped
                        // into an Action
                        Action agAction = new Action(hotelmania, registrationRequest);
                        getContentManager().fillContent(msg, agAction);
                        addBehaviour(new HandleRegistrationRequestResponse(hotelmania));
                        send(msg);
                    } catch (Codec.CodecException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    } catch (OntologyException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
                return null;
            }
        }));
    }

    private static class HandleRegistrationRequestResponse extends EmseSimpleBehaviour {

        Logger logger = LoggerFactory.getLogger(getClass());

        private final AID hotelMania;

        private boolean gotResponse = false;

        public HandleRegistrationRequestResponse (AID aid) {
            this.hotelMania = aid;
        }

        @Override protected List<MessageTemplate> getMessageTemplates () {
            return Arrays.asList(MessageTemplate.MatchSender(hotelMania));
        }

        @Override protected MessageStatus processMessage (ACLMessage message) {
            gotResponse = true;
            if (message.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
                logger.info("Received accept proposal as registration response");
                return MessageStatus.PROCESSED;
            } else if (message.getPerformative() == ACLMessage.REJECT_PROPOSAL) {
                logger.info("Received rejection as registration response");
                return MessageStatus.PROCESSED;
            } else if (message.getPerformative() == ACLMessage.NOT_UNDERSTOOD) {
                logger.info("Received not understood as registration response");
                return MessageStatus.PROCESSED;
            } else {
                gotResponse = false;
                logger.info("Received performative with code {}", message.getPerformative());
                return MessageStatus.NOT_PROCESSED;
            }
        }

        @Override public boolean done () {
            return gotResponse;
        }
    }


    private static class SearchForHotelMania extends OneShotBehaviour {

        private final Function<DFAgentDescription[], Object> onFound;

        public SearchForHotelMania (Agent a, Function<DFAgentDescription[], Object> f) {
            super(a);
            onFound = f;
        }

        @Override public void action () {
            DFAgentDescription dfd = Utils.createAgentDescriptionWithType(REGISTRATION);
            try {
                final DFAgentDescription[] search = DFService.search(getAgent(), dfd);
                if (search.length == 0) {
                    getAgent().doWait(5000);
                    getAgent().addBehaviour(new SearchForHotelMania(getAgent(), onFound));
                } else {
                    onFound.apply(search);
                }
            } catch (FIPAException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

    }
}
