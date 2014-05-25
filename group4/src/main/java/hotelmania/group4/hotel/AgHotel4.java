package hotelmania.group4.hotel;

import com.google.common.base.Optional;
import hotelmania.group4.HotelManiaAgent;
import hotelmania.group4.HotelManiaAgentNames;
import hotelmania.group4.platform.OnDayEvent;
import hotelmania.group4.utils.ProcessDescriptionFn;
import hotelmania.group4.utils.SearchForAgent;
import hotelmania.group4.utils.SubscribeToDayEvents;
import hotelmania.group4.utils.Utils;
import hotelmania.ontology.*;
import jade.content.lang.Codec;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.proto.SubscriptionInitiator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @author Alberth Montero <alberthm@gmail.com>
 * @author Tahir <tahircheema30@gmail.com>
 * @since 20/04/14
 */
public class AgHotel4 extends HotelManiaAgent {

    public static final String HOTEL_NAME = "Hotel4";

    private int accountId;

    Logger logger = LoggerFactory.getLogger(getClass());

    private SubscriptionInitiator dayEventsNotificationSubscriptionInitiator;

    private int day;

    @Override
    protected void setupHotelManiaAgent () {
        System.out.println(getLocalName() + ": HAS ENTERED");

        logger.debug("setting up agent");

        try {
            // Creates its own description
            DFAgentDescription dfd = Utils.createAgentDescriptionWithNameAndType(this.getName(), QUERY_NUMBER_OF_CLIENTS);
            // Registers its description in the DF
            DFService.register(this, dfd);
            logger.info(getLocalName() + ": registered in the DF");
        } catch (FIPAException e) {
            e.printStackTrace();
        }

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
                    hotel.setHotelAgent(getAID());
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

        final SubscribeToDayEvents subscribeToDayEvents = new SubscribeToDayEvents(this, new OnDayEvent() {
            @Override public void onDayEvent (NotificationDayEvent notificationDayEvent) {
                day = notificationDayEvent.getDayEvent().getDay();
                logger.info("Received new day event notification. Day {}", day);
            }
        });
        subscribeToDayEvents.doSubscription();

        // adding the SingContract behaviour for interacting with agency
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
                    contract.setDay(day + 1);

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


        // adding a behaviour for searching the bank
        addBehaviour(new SearchForAgent(HotelManiaAgentNames.CREATE_ACCOUNT, this, new ProcessDescriptionFn<Object>() {
            @Override public <T> Optional<T> found (
                    DFAgentDescription[] dfAgentDescriptions) throws Codec.CodecException, OntologyException {
                if (dfAgentDescriptions.length > 1) {
                    logger.error("More than 1 banks found");
                } else {
                    final DFAgentDescription dfAgentDescription = dfAgentDescriptions[0];
                    final AID bank = dfAgentDescription.getName();

                    ACLMessage newMessage = createMessage(bank, ACLMessage.REQUEST);
                    newMessage.setProtocol(CREATE_ACCOUNT);

                    CreateAccountRequest accountRequest = new CreateAccountRequest();
                    // making the account request and setting random the name of the hotel
                    Hotel hotel = new Hotel();
                    hotel.setHotel_name("Hotel4");
                    accountRequest.setHotel(hotel);


                    // As it is an action and the encoding language the SL, it must be wrapped
                    // into an Action
                    Action agentAction = new Action(bank, accountRequest);
                    getContentManager().fillContent(newMessage, agentAction);
                    addBehaviour(new HandleCreateAccountResponse(AgHotel4.this, bank));
                    sendMessage(newMessage);
                }
                return Optional.absent();
            }
        }));


        // adding behaviour for checking Account Status
        addBehaviour(new SearchForAgent(HotelManiaAgentNames.ACCOUNT_STATUS, this, new ProcessDescriptionFn<Object>() {
            @Override public <T> Optional<T> found (
                    DFAgentDescription[] dfAgentDescriptions) throws Codec.CodecException, OntologyException {
                if (dfAgentDescriptions.length > 1) {
                    logger.error("More than 1 Banks found");
                } else {
                    final DFAgentDescription dfAgentDescription = dfAgentDescriptions[0];
                    final AID bank = dfAgentDescription.getName();

                    ACLMessage newMessage = createMessage(bank, ACLMessage.REQUEST);
                    newMessage.setProtocol(ACCOUNT_STATUS);

                    // Creating a new AccountStatusQueryRef
                    AccountStatusQueryRef account = new AccountStatusQueryRef();
                    account.setId_account(accountId);

                    // As it is an action and the encoding language the SL, it must be wrapped
                    // into an Action
                    Action agentAction = new Action(bank, account);
                    getContentManager().fillContent(newMessage, agentAction);
                    addBehaviour(new HandleAccountStatusResponse(AgHotel4.this, bank));
                    sendMessage(newMessage);
                }
                return Optional.absent();
            }
        }));


        addBehaviour(new RespondToNumberOfClients(this));
        addBehaviour(new BookingOfferBehaviour(this));
        addBehaviour(new BookingBehaviour(this));


    }

    public void setAccountId (Integer accountId) {
        this.accountId = accountId;
    }
}
