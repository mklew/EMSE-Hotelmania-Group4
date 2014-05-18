package hotelmania.group4.utils;

import jade.core.Agent;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.SubscriptionResponder;

import java.util.UUID;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 18/05/14
 */
public class EmseSubscriptionResponder extends SubscriptionResponder {

    public EmseSubscriptionResponder (Agent a, MessageTemplate mt,
                                      SubscriptionManager sm) {
        super(a, mt, sm);
    }

    @Override
    protected ACLMessage handleSubscription (
            ACLMessage subscription) throws NotUnderstoodException, RefuseException {
        if (subscription.getConversationId() == null) {
            final String conversationId = subscription.getSender().getName() + " " + UUID.randomUUID().toString();
            subscription.setConversationId(conversationId);
        }
        final ACLMessage aclMessage = super.handleSubscription(subscription);
        if (aclMessage == null) {
            final ACLMessage reply = subscription.createReply();
            reply.setPerformative(ACLMessage.AGREE);
            return reply;
        } else {
            return aclMessage;
        }
    }
}
