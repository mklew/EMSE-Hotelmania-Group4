package hotelmania.group4.client;

import hotelmania.group4.HotelManiaAgent;
import hotelmania.group4.HotelManiaAgentNames;
import hotelmania.group4.behaviours.EmseSimpleBehaviour;
import hotelmania.group4.behaviours.MessageStatus;
import hotelmania.group4.utils.matchers.MessageHandler;
import hotelmania.group4.utils.matchers.MessageMatchingChain;
import hotelmania.group4.utils.matchers.PredicateHandler;
import hotelmania.ontology.BookingOffer;
import hotelmania.ontology.Stay;
import hotelmania.ontology.StayQueryRef;
import jade.content.lang.Codec;
import jade.content.onto.OntologyException;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Arrays;
import java.util.List;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 26/05/14
 */
public class AskForPrice extends EmseSimpleBehaviour {

    private final AID hotel;

    private final PriceOfferCb callback;

    private final Stay stay;

    public AskForPrice (HotelManiaAgent agent, AID hotel,
                        Stay stay, PriceOfferCb callback) {
        super(agent);
        this.hotel = hotel;
        this.callback = callback;
        this.stay = stay;
    }

    private boolean gotOfferOrRefused = false;

    @Override protected List<MessageTemplate> getMessageTemplates () {
        final MessageTemplate withCodec = MessageTemplate.MatchLanguage(getHotelManiaAgent().getCodec().getName());
        final MessageTemplate withOntology = MessageTemplate.MatchOntology(getHotelManiaAgent().getOntology().getName());
        final MessageTemplate withProtocolName = MessageTemplate.MatchProtocol(HotelManiaAgentNames.BOOKING_OFFER);

        return Arrays.asList(withCodec, withOntology, withProtocolName);
    }

    @Override
    protected MessageStatus processMessage (ACLMessage message) throws Codec.CodecException, OntologyException {
        final MessageMatchingChain messageMatchingChain = new MessageMatchingChain(getHotelManiaAgent()).withPredicateMatcher(BookingOffer.class, new PredicateHandler<BookingOffer>() {
            @Override public MessageStatus handle (BookingOffer predicate,
                                                   ACLMessage message) throws Codec.CodecException, OntologyException {
                callback.gotOffer(predicate);
                gotOfferOrRefused = true;
                return MessageStatus.PROCESSED;
            }
        }).withMessageHandler(new MessageHandler() {
            @Override public MessageStatus handle (ACLMessage message) throws OntologyException, Codec.CodecException {
                if (message.getPerformative() == ACLMessage.REFUSE) {
                    callback.refused();
                    gotOfferOrRefused = true;
                    return MessageStatus.PROCESSED;
                } else {
                    return MessageStatus.NOT_PROCESSED;
                }
            }
        });

        return messageMatchingChain.handleMessage(message);
    }

    @Override public boolean done () {
        return gotOfferOrRefused;
    }


    @Override protected void doAction () throws Codec.CodecException, OntologyException {
        final ACLMessage askForPrice = getHotelManiaAgent().createMessage(hotel, ACLMessage.QUERY_REF);
        askForPrice.setProtocol(HotelManiaAgentNames.BOOKING_OFFER);
        StayQueryRef stayQueryRef = new StayQueryRef();
        stayQueryRef.setStay(stay);

        getHotelManiaAgent().getContentManager().fillContent(askForPrice, stayQueryRef);
        getHotelManiaAgent().sendMessage(askForPrice);
    }

    public interface PriceOfferCb {
        void gotOffer (BookingOffer bookingOffer);

        void refused ();
    }
}
