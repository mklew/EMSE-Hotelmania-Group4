package hotelmania.group4.hotel;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import hotelmania.group4.HotelManiaAgent;
import hotelmania.group4.HotelManiaAgentNames;
import hotelmania.group4.utils.ProcessDescriptionFn;
import hotelmania.group4.utils.SearchForAgent;
import hotelmania.ontology.Contract;
import hotelmania.ontology.Hotel;
import hotelmania.ontology.RegistrationRequest;
import hotelmania.ontology.SignContract;
import jade.content.lang.Codec;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @author Alberth Montero <alberthm@gmail.com>
 * @author Tahir <tahircheema30@gmail.com>
 * @since 20/04/14
 */
public class AgHotel4 extends HotelManiaAgent {

    public static final String HOTEL_NAME = "Hotel4";
    Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected void setupHotelManiaAgent () {
        System.out.println(getLocalName() + ": HAS ENTERED");

        logger.debug("setting up agent");

        addBehaviour(new SearchForAgent(HotelManiaAgentNames.REGISTRATION, this, new ProcessDescriptionFn<Object>() {
            @Override public <T> Optional<T> found (
                    DFAgentDescription[] dfAgentDescriptions) throws Codec.CodecException, OntologyException {
                if (dfAgentDescriptions.length > 1) {
                    logger.error("More than 1 hotel mania agents found");
                } else {
                    final DFAgentDescription dfAgentDescription = dfAgentDescriptions[0];
                    final AID hotelmania = dfAgentDescription.getName();

                    ACLMessage msg = createMessage(hotelmania, ACLMessage.REQUEST);
                    msg.setProtocol(REGISTRATION);
                    RegistrationRequest registrationRequest = new RegistrationRequest();
                    final Hotel hotel = new Hotel();
                    hotel.setHotel_name(HOTEL_NAME);
                    registrationRequest.setHotel(hotel);

                    // As it is an action and the encoding language the SL, it must be wrapped
                    // into an Action
                    Action agAction = new Action(hotelmania, registrationRequest);
                    getContentManager().fillContent(msg, agAction);
                    addBehaviour(new HandleRegistrationRequestResponse(AgHotel4.this, hotelmania));
                    sendMessage(msg);
                }
                return Optional.absent();
            }
        }));

        // adding the SubscribeToDayEvent behaviour for interacting with Simulator
        //addBehaviour(SubscribeToDayEvent());
        addBehaviour(new SearchForAgent(HotelManiaAgentNames.SUBSCRIBE_TO_DAY_EVENT, this, new Function<DFAgentDescription[], Object>() {
            @Override public Object apply (DFAgentDescription[] dfAgentDescriptions) {

                if (dfAgentDescriptions.length > 1) {
                    logger.error("More than 1 simulator found");
                } else {
                    final DFAgentDescription dfAgentDescription = dfAgentDescriptions[0];
                    final AID simulator = dfAgentDescription.getName();

                    ACLMessage msg = createMessage(simulator, ACLMessage.SUBSCRIBE);
                    msg.setProtocol(SUBSCRIBE_TO_DAY_EVENT);
                    // We want to receive a reply in 10 secs
                    msg.setReplyByDate(new Date(System.currentTimeMillis() + 10000));
                    msg.setContent("SubscribeToDayEvent");
                    //SubscribeToDayEvent subscribeToDayEvent = new SubscribeToDayEvent();
                    //SignContract signContract = new SignContract();
                    //try {
                    addBehaviour(new AchieveREInitiator(AgHotel4.this, msg) {
                        protected void handleInform (ACLMessage inform) {
                            System.out.println("Agent " + inform.getSender().getName() + " successfully performed the requested action");
                        }

                        protected void handleRefuse (ACLMessage refuse) {
                            System.out.println("Agent " + refuse.getSender().getName() + " refused to perform the requested action");
                        }

                        protected void handleFailure (ACLMessage failure) {
                            if (failure.getSender().equals(myAgent.getAMS())) {
                                // FAILURE notification from the JADE runtime: the receiver
                                // does not exist
                                System.out.println("Responder does not exist");
                            } else {
                                System.out.println("Agent " + failure.getSender().getName() + " failed to perform the requested action");
                            }
                        }
                    });

                    // As it is an action and the encoding language the SL, it must be wrapped
                    // into an Action
                        /*Action agentAction = new Action(simulator, subscribeToDayEvent);
                        getContentManager().fillContent(newMessage, agentAction);
                        addBehaviour(new HandleSignContractResponse(AgHotel4.this, simulator));
                        sendMessage(newMessage);
                    } catch ( e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    } catch (OntologyException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }*/
                }
                return null;
            }
        }));

        // adding the SingContract behaviour for interacting with agency
        //addBehaviour(new SignContract());
        addBehaviour(new SearchForAgent(HotelManiaAgentNames.SIGN_CONTRACT, this, new ProcessDescriptionFn<Object>() {
            @Override public <T> Optional<T> found (
                    DFAgentDescription[] dfAgentDescriptions) throws Codec.CodecException, OntologyException {
                if (dfAgentDescriptions.length > 1) {
                    logger.error("More than 1 agencies found");
                } else {
                    final DFAgentDescription dfAgentDescription = dfAgentDescriptions[0];
                    final AID agency = dfAgentDescription.getName();

                    ACLMessage newMessage = createMessage(agency, ACLMessage.REQUEST);
                    newMessage.setProtocol(SIGN_CONTRACT);
                    SignContract signContract = new SignContract();
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
                }
                return Optional.absent();
            }
        }));
    }


}
