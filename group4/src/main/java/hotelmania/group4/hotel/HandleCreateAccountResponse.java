package hotelmania.group4.hotel;

import hotelmania.group4.behaviours.EmseSimpleBehaviour;
import hotelmania.group4.behaviours.MessageStatus;
import hotelmania.group4.utils.matchers.MessageHandler;
import hotelmania.group4.utils.matchers.MessageMatchingChain;
import hotelmania.ontology.AccountStatus;
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
 * Created by Tahir on 09/05/2014.
 */
class HandleCreateAccountResponse extends EmseSimpleBehaviour {

    private final AgHotel4.OnDone onDone;

    Logger logger = LoggerFactory.getLogger(getClass());

    private final AID bank;

    private boolean gotResponse = false;

    private AgHotel4 agHotel4;

    public HandleCreateAccountResponse (AgHotel4 agHotel4, AID aid, AgHotel4.OnDone onDone) {
        super(agHotel4);
        this.agHotel4 = agHotel4;
        this.bank = aid;
        this.onDone = onDone;
    }

    @Override protected List<MessageTemplate> getMessageTemplates () {
        return Arrays.asList(MessageTemplate.MatchSender(bank));
    }

    @Override
    protected MessageStatus processMessage (ACLMessage message) throws Codec.CodecException, OntologyException {
        final MessageMatchingChain messageMatchingChain = new MessageMatchingChain(getAgent()).withMessageHandler(new MessageHandler() {
            @Override public MessageStatus handle (ACLMessage message) throws OntologyException, Codec.CodecException {
                if (message.getPerformative() == ACLMessage.INFORM) {
                    final AccountStatus accountStatus = AccountStatus.class.cast(getHotelManiaAgent().getContentManager().extractContent(message));
                    final int accountId = accountStatus.getAccount().getId_account();
                    logger.info("Received INFORM for successful CreateAccount. Account ID is {}", accountId);
                    agHotel4.setAccountId(accountId);
                    gotResponse = true;
                    onDone.done();
                    return MessageStatus.PROCESSED;
                } else if (message.getPerformative() == ACLMessage.FAILURE) {
                    logger.info("Received FAILURE as CreateAccount response");
                    gotResponse = true;
                    onDone.failed();
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

}
