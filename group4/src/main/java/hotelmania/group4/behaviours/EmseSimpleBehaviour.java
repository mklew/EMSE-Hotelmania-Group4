package hotelmania.group4.behaviours;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import hotelmania.group4.HotelManiaAgent;
import hotelmania.group4.utils.Utils;
import jade.content.lang.Codec;
import jade.content.onto.OntologyException;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * This is just better base class for simple behaviour because it behaves correctly and blocks for the message.
 *
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 20/04/14
 */
public abstract class EmseSimpleBehaviour extends SimpleBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(EmseSimpleBehaviour.class);

    private HotelManiaAgent agent;

    public EmseSimpleBehaviour (HotelManiaAgent agent) {
        super(agent);
        Preconditions.checkNotNull(agent);
        this.agent = agent;
    }

    @Override
    public void action () {
        try {
            doAction();
        } catch (Codec.CodecException e) {
            throw new RuntimeException(e);
        } catch (OntologyException e) {
            throw new RuntimeException(e);
        }
        final List<MessageTemplate> messageTemplates = getMessageTemplates();
        ACLMessage receive;
        if(messageTemplates.isEmpty()) {
            receive = getAgent().receive();
        }
        else {
            receive = getAgent().receive(Utils.messageTemplateConjunction(messageTemplates));
        }
        final Optional<ACLMessage> aclMessageOptional = Optional.fromNullable(receive);
        if (aclMessageOptional.isPresent()) {
            logger.debug("Received message {}", receive.toString());
            final ACLMessage message = aclMessageOptional.get();
            MessageStatus messageStatus = MessageStatus.NOT_PROCESSED;
            try {
                messageStatus = processMessage(message);
            } catch (Codec.CodecException | OntologyException e) {
                logger.error("Error", e);
            }
            if (messageStatus.equals(MessageStatus.NOT_PROCESSED)) {
                logger.debug("Message has not been processed. Putting it back to message queue for other behaviours");
                getAgent().putBack(message);
            }
        } else {
            block();
        }
    }

    protected abstract List<MessageTemplate> getMessageTemplates ();

    protected abstract MessageStatus processMessage (ACLMessage message) throws Codec.CodecException, OntologyException;

    protected void doAction() throws Codec.CodecException, OntologyException {

    }

    protected HotelManiaAgent getHotelManiaAgent () {
        return agent;
    }

    protected void sendMessage (ACLMessage message) {
        getHotelManiaAgent().sendMessage(message);
    }
}
