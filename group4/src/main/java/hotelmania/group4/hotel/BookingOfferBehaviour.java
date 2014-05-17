package hotelmania.group4.hotel;

import com.google.inject.Inject;
import hotelmania.group4.HotelManiaAgent;
import hotelmania.group4.HotelManiaAgentNames;
import hotelmania.group4.behaviours.EmseCyclicBehaviour;
import hotelmania.group4.behaviours.MessageStatus;
import hotelmania.group4.domain.Hotel4;
import hotelmania.group4.domain.NoRoomsAvailableException;
import hotelmania.group4.guice.GuiceConfigurer;
import hotelmania.group4.utils.MessageHandler;
import hotelmania.group4.utils.MessageMatchingChain;
import hotelmania.ontology.Price;
import hotelmania.ontology.Stay;
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
        final MessageTemplate withRequestPerformative = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
        final MessageTemplate withProtocolName = MessageTemplate.MatchProtocol(HotelManiaAgentNames.BOOKING_OFFER);

        return Arrays.asList(withCodec, withOntology, withRequestPerformative, withProtocolName);
    }

    @Override
    protected MessageStatus processMessage (ACLMessage message) throws Codec.CodecException, OntologyException {
        final MessageMatchingChain messageMatchingChain = new MessageMatchingChain(getHotelManiaAgent()).withMessageHandler(new MessageHandler() {
            @Override public MessageStatus handle (ACLMessage message) throws OntologyException, Codec.CodecException {
                final ContentElement contentElement = getAgent().getContentManager().extractContent(message);
                final Stay stay = Stay.class.cast(contentElement);

                try {
                    final Price priceFor = hotel.getPriceFor(stay);
                    // TODO send response
                } catch (NoRoomsAvailableException e) {
                    // TODO send refuse
                }
                return MessageStatus.PROCESSED;
            }
        });
        return messageMatchingChain.handleMessage(message);
    }
}
