package hotelmania.group4.behaviours;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import hotelmania.group4.HotelManiaAgent;
import hotelmania.group4.utils.Utils;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.List;

/**
 * This is just better base class for simple behaviour because it behaves correctly and blocks for the message.
 *
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 20/04/14
 */
public abstract class EmseSimpleBehaviour extends SimpleBehaviour {

    private HotelManiaAgent agent;

    public EmseSimpleBehaviour (HotelManiaAgent agent) {
        super(agent);
        Preconditions.checkNotNull(agent);
        this.agent = agent;
    }

    @Override
    public void action () {
        final List<MessageTemplate> messageTemplates = getMessageTemplates();
        final ACLMessage receive = getAgent().receive(Utils.messageTemplateConjunction(messageTemplates));
        final Optional<ACLMessage> aclMessageOptional = Optional.fromNullable(receive);
        if (aclMessageOptional.isPresent()) {
            final ACLMessage message = aclMessageOptional.get();
            final MessageStatus messageStatus = processMessage(message);
            if (messageStatus.equals(MessageStatus.NOT_PROCESSED)) {
                getAgent().putBack(message);
            }
        } else {
            block();
        }
    }

    protected abstract List<MessageTemplate> getMessageTemplates ();

    protected abstract MessageStatus processMessage (ACLMessage message);

    protected HotelManiaAgent getHotelManiaAgent () {
        return agent;
    }

    protected void sendMessage (ACLMessage message) {
        getHotelManiaAgent().sendMessage(message);
    }
}
