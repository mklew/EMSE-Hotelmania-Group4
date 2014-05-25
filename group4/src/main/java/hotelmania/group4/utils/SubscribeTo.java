package hotelmania.group4.utils;

import com.google.common.base.Function;
import hotelmania.group4.HotelManiaAgent;
import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.onto.OntologyException;
import jade.core.AID;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.proto.SubscriptionInitiator;

import java.util.Date;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 25/05/14
 */
public abstract class SubscribeTo {

    protected String subscribeToName;

    SubscriptionInitiator subscriptionBehaviour;

    final HotelManiaAgent agent;

    public SubscribeTo (
            String subscribeToName, final HotelManiaAgent agent) {
        this.subscribeToName = subscribeToName;
        this.agent = agent;
    }

    public void doSubscription () {

        agent.addBehaviour(new SearchForAgent(subscribeToName, agent, new Function<DFAgentDescription[], Object>() {
            @Override public Object apply (DFAgentDescription[] dfAgentDescriptions) {

                if (dfAgentDescriptions.length > 1) {
                    throw new RuntimeException("More than 1 simulator found");
                } else {
                    final DFAgentDescription dfAgentDescription = dfAgentDescriptions[0];
                    final AID simulator = dfAgentDescription.getName();
                    final ACLMessage subscriptionMessage = createSubscriptionMessage(simulator, agent);
                    createSubscriptionInitiator(subscriptionMessage, agent);

                    agent.addBehaviour(subscriptionBehaviour);
                }
                return null;
            }
        }));
    }

    private void createSubscriptionInitiator (final ACLMessage msg, HotelManiaAgent agent) {
        subscriptionBehaviour = new SubscriptionInitiator(agent, msg) {
            @Override protected void handleInform (ACLMessage inform) {
                super.handleInform(inform);
                ContentElement content = null;
                try {
                    content = getAgent().getContentManager().extractContent(inform);
                } catch (Codec.CodecException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (OntologyException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                handleContent(content);
            }
        };
    }

    protected abstract void handleContent (ContentElement content);

    private ACLMessage createSubscriptionMessage (AID simulator, HotelManiaAgent agent) {
        ACLMessage msg = agent.createMessage(simulator, ACLMessage.SUBSCRIBE);
        msg.setProtocol(subscribeToName);
        // We want to receive a reply in 10 secs
        msg.setReplyByDate(new Date(System.currentTimeMillis() + 10000));
        msg.setContent(subscribeToName);
        return msg;
    }

    public void unsubscribe () {
        agent.removeBehaviour(subscriptionBehaviour);
    }
}
