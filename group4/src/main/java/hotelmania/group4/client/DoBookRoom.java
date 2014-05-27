package hotelmania.group4.client;

import hotelmania.group4.HotelManiaAgent;
import hotelmania.group4.HotelManiaAgentNames;
import hotelmania.group4.behaviours.EmseSimpleBehaviour;
import hotelmania.group4.behaviours.MessageStatus;
import hotelmania.group4.utils.matchers.MessageHandler;
import hotelmania.group4.utils.matchers.MessageMatchingChain;
import hotelmania.ontology.BookRoom;
import jade.content.lang.Codec;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Arrays;
import java.util.List;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 27/05/14
 */
public class DoBookRoom extends EmseSimpleBehaviour {

    public interface BookResult {
        void success ();

        void fail ();
    }

    private final AID hotel;

    private final BookRoom bookRoom;

    private final BookResult resultCallback;

    private boolean gotResponse = false;

    public DoBookRoom (HotelManiaAgent agent, AID hotel, BookRoom bookRoom,
                       BookResult resultCallback) {
        super(agent);
        this.hotel = hotel;
        this.bookRoom = bookRoom;
        this.resultCallback = resultCallback;
    }

    @Override protected List<MessageTemplate> getMessageTemplates () {
        final MessageTemplate withCodec = MessageTemplate.MatchLanguage(getHotelManiaAgent().getCodec().getName());
        final MessageTemplate withOntology = MessageTemplate.MatchOntology(getHotelManiaAgent().getOntology().getName());
        final MessageTemplate withProtocolName = MessageTemplate.MatchProtocol(HotelManiaAgentNames.BOOK_A_ROOM);

        return Arrays.asList(withCodec, withOntology, withProtocolName);
    }

    @Override
    protected MessageStatus processMessage (ACLMessage message) throws Codec.CodecException, OntologyException {
        final MessageMatchingChain messageMatchingChain = new MessageMatchingChain(getHotelManiaAgent()).withMessageHandler(new MessageHandler() {
            @Override public MessageStatus handle (ACLMessage message) throws OntologyException, Codec.CodecException {
                gotResponse = true;
                if (message.getPerformative() == ACLMessage.AGREE) {
                    resultCallback.success();
                    return MessageStatus.PROCESSED;
                } else if (message.getPerformative() == ACLMessage.REFUSE) {
                    resultCallback.fail();
                    return MessageStatus.PROCESSED;
                } else {
                    return MessageStatus.NOT_PROCESSED;
                }
            }
        });

        return messageMatchingChain.handleMessage(message);
    }

    @Override public boolean done () {
        return gotResponse;
    }

    @Override protected void doAction () throws Codec.CodecException, OntologyException {
        final ACLMessage askForInformation = getHotelManiaAgent().createMessage(hotel, ACLMessage.REQUEST);
        askForInformation.setProtocol(HotelManiaAgentNames.BOOK_A_ROOM);

        Action agAction = new Action(hotel, bookRoom);
        getHotelManiaAgent().getContentManager().fillContent(askForInformation, agAction);

        getHotelManiaAgent().sendMessage(askForInformation);
    }
}
