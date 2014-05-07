package hotelmania.group4.hotel;

import com.google.common.base.Function;
import hotelmania.group4.HotelManiaAgent;
import hotelmania.group4.behaviours.EmseSimpleBehaviour;
import hotelmania.group4.behaviours.MessageStatus;
import hotelmania.group4.utils.MessageHandler;
import hotelmania.group4.utils.MessageMatchingChain;
import hotelmania.group4.utils.Utils;
import hotelmania.ontology.Contract;
import hotelmania.ontology.Hotel;
import hotelmania.ontology.RegistrationRequest;
import hotelmania.ontology.SignContract;
import jade.content.lang.Codec;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;
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
 * @author Tahir <tahircheema30@gmail.com>
 * @since 20/04/14
 */
public class AgHotel4 extends HotelManiaAgent {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected void setupHotelManiaAgent () {
        System.out.println(getLocalName()+": HAS ENTERED");

        logger.debug("setting up agent");

        addBehaviour(new SearchForHotelMania(this, new Function<DFAgentDescription[], Object>() {
            @Override public Object apply (DFAgentDescription[] dfAgentDescriptions) {

                if (dfAgentDescriptions.length > 1) {
                    logger.error("More than 1 hotel mania agents found");
                } else {
                    final DFAgentDescription dfAgentDescription = dfAgentDescriptions[0];
                    final AID hotelmania = dfAgentDescription.getName();

                    ACLMessage msg = createMessage(hotelmania, ACLMessage.REQUEST);
                    msg.setProtocol(REGISTRATION);
                    RegistrationRequest registrationRequest = new RegistrationRequest();
                    try {
                        final Hotel hotel = new Hotel();
                        hotel.setHotel_name("Hotel4"); // TODO extract that
                        registrationRequest.setHotel(hotel);

                        // As it is an action and the encoding language the SL, it must be wrapped
                        // into an Action
                        Action agAction = new Action(hotelmania, registrationRequest);
                        getContentManager().fillContent(msg, agAction);
                        addBehaviour(new HandleRegistrationRequestResponse(AgHotel4.this, hotelmania));
                        sendMessage(msg);
                    } catch (Codec.CodecException e) {
                        e.printStackTrace();
                    } catch (OntologyException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        }));

        // adding the SingContract behaviour for interacting with agency
        //addBehaviour(new SignContract());
        addBehaviour(new SearchForAgency(this, new Function<DFAgentDescription[], Object>() {
            @Override public Object apply (DFAgentDescription[] dfAgentDescriptions) {

                if (dfAgentDescriptions.length > 1) {
                    logger.error("More than 1 agencies found");
                } else {
                    final DFAgentDescription dfAgentDescription = dfAgentDescriptions[0];
                    final AID agency = dfAgentDescription.getName();

                    ACLMessage newMessage = createMessage(agency, ACLMessage.REQUEST);
                    newMessage.setProtocol(SIGNCONTRACT);
                    SignContract signContract = new SignContract();
                    try {
                        // making the contract and setting random values for the attributes
                        final Contract contract = new Contract();
                        contract.setChef_1stars(2);
                        contract.setChef_2stars(2);
                        contract.setChef_3stars(3);
                        contract.setRecepcionist_experienced(1);
                        contract.setRecepcionist_novice(2);
                        contract.setRoom_service_staff(6);

                        // making a new hotel and setting its name as "Hotel4"
                        final Hotel hotel = new Hotel();
                        hotel.setHotel_name("Hotel4");

                        // setting the contract and hotel for 'SignContract'
                        signContract.setHotel(hotel);
                        signContract.setContract(contract);

                        // As it is an action and the encoding language the SL, it must be wrapped
                        // into an Action
                        Action agentAction = new Action(agency, signContract);
                        getContentManager().fillContent(newMessage, agentAction);
                        addBehaviour(new HandleSignContractResponse(AgHotel4.this, agency));
                        sendMessage(newMessage);
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

        public HandleRegistrationRequestResponse (AgHotel4 agHotel4, AID aid) {
            super(agHotel4);
            this.hotelMania = aid;
        }


        @Override protected List<MessageTemplate> getMessageTemplates () {
            return Arrays.asList(MessageTemplate.MatchSender(hotelMania));
        }

        @Override protected MessageStatus processMessage (ACLMessage message) {
            final MessageMatchingChain messageMatchingChain = new MessageMatchingChain(getAgent()).withMessageHandler(new MessageHandler() {
                @Override public MessageStatus handle (ACLMessage message) {
                    if (message.getPerformative() == ACLMessage.AGREE) {
                        logger.info("Received agree as registration response");
                        return MessageStatus.PROCESSED;
                    } else if (message.getPerformative() == ACLMessage.REFUSE) {
                        logger.info("Received refuse as registration response");
                        return MessageStatus.PROCESSED;
                    } else if (message.getPerformative() == ACLMessage.NOT_UNDERSTOOD) {
                        logger.info("Received not understood as registration response");
                        return MessageStatus.PROCESSED;
                    } else {
                        gotResponse = false;
                        final ACLMessage reply = getHotelManiaAgent().createReply(message);
                        reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
                        logger.info("Received unexpected message with performative with code {}. Replying with NOT_UNDERSTOOD", message.getPerformative());
                        sendMessage(reply);
                        return MessageStatus.PROCESSED;
                    }
                }
            });
            return messageMatchingChain.handleMessage(message);
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


    // Internal class for searching the agency behavior
    private static class SearchForAgency extends OneShotBehaviour {

        private final Function<DFAgentDescription[], Object> onFound;

        // Method for searching the agency
        public SearchForAgency (Agent a, Function<DFAgentDescription[], Object> f) {
            super(a);
            onFound = f;
        }
        // overriding the action() method of OneShotBehaviour
        @Override public void action () {
            DFAgentDescription dfd = Utils.createAgentDescriptionWithType(SIGNCONTRACT);
            try {
                final DFAgentDescription[] search = DFService.search(getAgent(), dfd);
                if (search.length == 0) {
                    getAgent().doWait(5000);
                    getAgent().addBehaviour(new SearchForAgency(getAgent(), onFound));
                } else {
                    onFound.apply(search);
                }
            } catch (FIPAException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

    }


    private static class HandleSignContractResponse extends EmseSimpleBehaviour {

        Logger logger = LoggerFactory.getLogger(getClass());

        private final AID agency;

        private boolean gotResponse = false;

        public HandleSignContractResponse (AgHotel4 agHotel4, AID aid) {
            super(agHotel4);
            this.agency = aid;
        }


        @Override protected List<MessageTemplate> getMessageTemplates () {
            return Arrays.asList(MessageTemplate.MatchSender(agency));
        }

        @Override protected MessageStatus processMessage (ACLMessage message) {
            final MessageMatchingChain messageMatchingChain = new MessageMatchingChain(getAgent()).withMessageHandler(new MessageHandler() {
                @Override public MessageStatus handle (ACLMessage message) {
                    if (message.getPerformative() == ACLMessage.AGREE) {
                        logger.info("Received agree as SignContract response");
                        gotResponse = true;
                        return MessageStatus.PROCESSED;
                    } else if (message.getPerformative() == ACLMessage.REFUSE) {
                        logger.info("Received refuse as SignContract response");
                        gotResponse = true;
                        return MessageStatus.PROCESSED;
                    } else if (message.getPerformative() == ACLMessage.NOT_UNDERSTOOD) {
                        logger.info("Received not understood as SignContract response");
                        gotResponse = true;
                        return MessageStatus.PROCESSED;
                    } else {
                        gotResponse = false;
                        final ACLMessage reply = getHotelManiaAgent().createReply(message);
                        reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
                        logger.info("Received unexpected message with performative with code {}. Replying with NOT_UNDERSTOOD", message.getPerformative());
                        sendMessage(reply);
                        return MessageStatus.PROCESSED;
                    }
                }
            });
            return messageMatchingChain.handleMessage(message);
        }

        @Override public boolean done () {
            return gotResponse;
        }
    }
}
