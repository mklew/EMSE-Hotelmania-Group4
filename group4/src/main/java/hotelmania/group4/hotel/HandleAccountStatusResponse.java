package hotelmania.group4.hotel;

import hotelmania.group4.behaviours.EmseCyclicBehaviour;
import hotelmania.group4.behaviours.MessageStatus;
import hotelmania.group4.utils.MessageHandler;
import hotelmania.group4.utils.MessageMatchingChain;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Created by azfar on 12/05/2014.
 */
public class HandleAccountStatusResponse extends EmseCyclicBehaviour {
    Logger logger = LoggerFactory.getLogger(getClass());

    private final AID bank;

    private boolean gotResponse = false;

    public HandleAccountStatusResponse (AgHotel4 agHotel4, AID aid) {
        super(agHotel4);
        this.bank = aid;
    }

    @Override protected List<MessageTemplate> getMessageTemplates () {
        return Arrays.asList(MessageTemplate.MatchSender(bank));
    }

    @Override protected MessageStatus processMessage (ACLMessage message) {
        final MessageMatchingChain messageMatchingChain = new MessageMatchingChain(getAgent()).withMessageHandler(new MessageHandler() {
            @Override public MessageStatus handle (ACLMessage message) {
                if (message.getPerformative() == ACLMessage.INFORM) {

                    // TODO: somehow setting this message.getContent() as the the ACCOUNT_ID of hotel

                    logger.info("Received INFORM for successful CreateAccount");
                    gotResponse = true;
                    return MessageStatus.PROCESSED;
                } else if (message.getPerformative() == ACLMessage.FAILURE) {
                    logger.info("Received FAILURE as CreateAccount response");
                    gotResponse = true;
                    return MessageStatus.PROCESSED;
                } else if (message.getPerformative() == ACLMessage.NOT_UNDERSTOOD) {
                    logger.info("Received not understood as CreateAccount response");
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
