package hotelmania.group4.hotel;

import hotelmania.group4.behaviours.EmseSimpleBehaviour;
import hotelmania.group4.behaviours.MessageStatus;
import hotelmania.group4.utils.MessageHandler;
import hotelmania.group4.utils.MessageMatchingChain;
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
class HandleSignContractResponse extends EmseSimpleBehaviour {

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

    @Override protected MessageStatus processMessage (ACLMessage message) throws Codec.CodecException, OntologyException {
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
