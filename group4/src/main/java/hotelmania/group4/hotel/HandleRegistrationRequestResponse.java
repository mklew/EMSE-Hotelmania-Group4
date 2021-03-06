package hotelmania.group4.hotel;

import hotelmania.group4.HotelManiaAgentNames;
import hotelmania.group4.behaviours.EmseSimpleBehaviour;
import hotelmania.group4.behaviours.MessageStatus;
import hotelmania.group4.utils.matchers.MessageHandler;
import hotelmania.group4.utils.matchers.MessageMatchingChain;
import jade.content.lang.Codec;
import jade.content.onto.OntologyException;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 08/05/14
 */
class HandleRegistrationRequestResponse extends EmseSimpleBehaviour {

    Logger logger = LoggerFactory.getLogger(getClass());

    private final AID hotelMania;

    private boolean gotResponse = false;

    public HandleRegistrationRequestResponse (AgHotel4 agHotel4, AID aid) {
        super(agHotel4);
        this.hotelMania = aid;
    }


    @Override protected List<MessageTemplate> getMessageTemplates () {
        final MessageTemplate messageTemplate = MessageTemplate.MatchSender(hotelMania);
        final MessageTemplate withProtocolName = MessageTemplate.MatchProtocol(HotelManiaAgentNames.REGISTRATION);
        return Arrays.asList(messageTemplate, withProtocolName);
    }

    @Override protected MessageStatus processMessage (ACLMessage message) throws Codec.CodecException, OntologyException {
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
