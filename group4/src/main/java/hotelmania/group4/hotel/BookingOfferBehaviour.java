package hotelmania.group4.hotel;

import com.google.inject.Inject;
import hotelmania.group4.HotelManiaAgent;
import hotelmania.group4.HotelManiaAgentNames;
import hotelmania.group4.behaviours.EmseCyclicBehaviour;
import hotelmania.group4.behaviours.MessageStatus;
import hotelmania.group4.domain.Hotel4;
import hotelmania.group4.domain.NoRoomsAvailableException;
import hotelmania.group4.guice.GuiceConfigurer;
import hotelmania.group4.utils.matchers.MessageHandler;
import hotelmania.group4.utils.matchers.MessageMatchingChain;
import hotelmania.ontology.BookingOffer;
import hotelmania.ontology.Price;
import hotelmania.ontology.Stay;
import hotelmania.ontology.StayQueryRef;
import jade.content.ContentElement;
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
 * @since 17/05/14
 */
public class BookingOfferBehaviour extends EmseCyclicBehaviour {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Inject
    Hotel4 hotel;

    public BookingOfferBehaviour (HotelManiaAgent agent) {
        super(agent);
        GuiceConfigurer.getInjector().injectMembers(this);
    }

    @Override protected List<MessageTemplate> getMessageTemplates () {
        final MessageTemplate withCodec = MessageTemplate.MatchLanguage(getHotelManiaAgent().getCodec().getName());
        final MessageTemplate withOntology = MessageTemplate.MatchOntology(getHotelManiaAgent().getOntology().getName());
        final MessageTemplate withRequestPerformative = MessageTemplate.MatchPerformative(ACLMessage.QUERY_REF);
        final MessageTemplate withProtocolName = MessageTemplate.MatchProtocol(HotelManiaAgentNames.BOOKING_OFFER);

        return Arrays.asList(withCodec, withOntology, withRequestPerformative, withProtocolName);
    }

    @Override
    protected MessageStatus processMessage (ACLMessage message) throws Codec.CodecException, OntologyException {
        final MessageMatchingChain messageMatchingChain = new MessageMatchingChain(getHotelManiaAgent()).withMessageHandler(new MessageHandler() {
            @Override public MessageStatus handle (ACLMessage message) throws OntologyException, Codec.CodecException {
                final ContentElement contentElement = getAgent().getContentManager().extractContent(message);
                final StayQueryRef stayQueryRef = StayQueryRef.class.cast(contentElement);
                final Stay stay = stayQueryRef.getStay();

                final ACLMessage reply = getHotelManiaAgent().createReply(message);
                reply.setProtocol(HotelManiaAgentNames.BOOKING_OFFER);
                try {
                    reply.setPerformative(ACLMessage.INFORM);

                    final Price priceFor = hotel.getPriceFor(stay);
                    final BookingOffer bookingOffer = new BookingOffer();
                    bookingOffer.setRoomPrice(priceFor);
                    logger.info("Sending INFORM BookingOffer to agent {}", message.getSender().getName());
                    getHotelManiaAgent().getContentManager().fillContent(reply, bookingOffer);
                } catch (NoRoomsAvailableException e) {
                    logger.info("No rooms available, sending REFUSE BookingOffer to agent {}", message.getSender().getName());
                    reply.setPerformative(ACLMessage.REFUSE);
                }

                getHotelManiaAgent().sendMessage(reply);
                return MessageStatus.PROCESSED;
            }
        });
        return messageMatchingChain.handleMessage(message);
    }
}
