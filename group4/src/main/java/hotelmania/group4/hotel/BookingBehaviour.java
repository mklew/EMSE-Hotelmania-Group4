package hotelmania.group4.hotel;

import com.google.inject.Inject;
import hotelmania.group4.HotelManiaAgent;
import hotelmania.group4.HotelManiaAgentNames;
import hotelmania.group4.behaviours.EmseCyclicBehaviour;
import hotelmania.group4.behaviours.MessageStatus;
import hotelmania.group4.domain.CurrentPriceIsHigherException;
import hotelmania.group4.domain.Hotel4;
import hotelmania.group4.domain.NoRoomsAvailableException;
import hotelmania.group4.guice.GuiceConfigurer;
import hotelmania.group4.utils.ActionMessageHandler;
import hotelmania.group4.utils.MessageMatchingChain;
import hotelmania.ontology.BookRoom;
import hotelmania.ontology.BookingOffer;
import hotelmania.ontology.Stay;
import jade.content.lang.Codec;
import jade.content.onto.OntologyException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 18/05/14
 */
public class BookingBehaviour extends EmseCyclicBehaviour {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Inject
    private Hotel4 hotel4;

    public BookingBehaviour (HotelManiaAgent agHotel4) {
        super(agHotel4);
        GuiceConfigurer.getInjector().injectMembers(this);
    }

    @Override protected List<MessageTemplate> getMessageTemplates () {
        final MessageTemplate withCodec = MessageTemplate.MatchLanguage(getHotelManiaAgent().getCodec().getName());
        final MessageTemplate withOntology = MessageTemplate.MatchOntology(getHotelManiaAgent().getOntology().getName());
        final MessageTemplate withRequestPerformative = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
        final MessageTemplate withProtocolName = MessageTemplate.MatchProtocol(HotelManiaAgentNames.BOOK_A_ROOM);

        return Arrays.asList(withCodec, withOntology, withRequestPerformative, withProtocolName);
    }

    @Override
    protected MessageStatus processMessage (ACLMessage message) throws Codec.CodecException, OntologyException {
        final MessageMatchingChain messageMatchingChain = new MessageMatchingChain(getAgent()).withActionMatcher(BookRoom.class, new ActionMessageHandler<BookRoom>() {
            @Override
            public MessageStatus handle (BookRoom bookRoomAction,
                                         ACLMessage message) throws Codec.CodecException, OntologyException {
                final BookingOffer bookingOffer = bookRoomAction.getBookingOffer();
                final Stay stay = bookRoomAction.getStay();

                final ACLMessage reply = getHotelManiaAgent().createReply(message);
                reply.setProtocol(HotelManiaAgentNames.BOOK_A_ROOM);
                reply.setPerformative(ACLMessage.AGREE);
                try {
                    hotel4.bookRoomFor(stay, bookingOffer.getRoomPrice());
                    logger.info("Booked room for stay from day {} to day {} for agent {}", stay.getCheckIn(), stay.getCheckOut(), message.getSender().getName());
                } catch (NoRoomsAvailableException e) {
                    logger.info("Room cannot be booked because there are no rooms available. Sending REFUSE BookARoom");
                    reply.setPerformative(ACLMessage.REFUSE);
                } catch (CurrentPriceIsHigherException e) {
                    logger.info("Room cannot be booked because current price is higher. Current price is {} and clients wants to pay {} Sending REFUSE BookARoom", e.getCurrentPrice(), e.getAgreedPrice());
                    reply.setPerformative(ACLMessage.REFUSE);
                }
                getHotelManiaAgent().sendMessage(reply);
                return MessageStatus.PROCESSED;
            }
        });

        return messageMatchingChain.handleMessage(message);
    }
}
