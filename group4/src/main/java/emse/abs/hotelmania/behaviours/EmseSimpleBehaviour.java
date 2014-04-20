package emse.abs.hotelmania.behaviours;

import com.google.common.base.Optional;
import emse.abs.hotelmania.group4.Utils;
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

    @Override
    public void action () {
        final List<MessageTemplate> messageTemplates = getMessageTemplates();
        final ACLMessage receive = getAgent().receive(Utils.messageTemplateConjunction(messageTemplates));
        final Optional<ACLMessage> aclMessageOptional = Optional.fromNullable(receive);
        if (aclMessageOptional.isPresent()) {
            processMessage(aclMessageOptional.get());
        } else {
            block();
        }
    }

    protected abstract List<MessageTemplate> getMessageTemplates ();

    protected abstract void processMessage (ACLMessage message);
}
