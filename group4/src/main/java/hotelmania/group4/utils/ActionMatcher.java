package hotelmania.group4.utils;

import hotelmania.group4.behaviours.MessageStatus;
import jade.content.Concept;
import jade.content.ContentElement;
import jade.content.onto.basic.Action;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

/**
 * Template to handle message
 *
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 21/04/14
 */
public class ActionMatcher<T> implements MessageProcessor<T> {

    final Class<T> actionClass;

    private final Agent agent;

    private final ActionMessageHandler<T> handler;

    public ActionMatcher (Class<T> actionClass, Agent agent, ActionMessageHandler<T> handler) {
        this.actionClass = actionClass;
        this.agent = agent;
        this.handler = handler;
    }

    @Override public MessageStatus tryToHandle (ACLMessage message) {
        try {
            ContentElement content = getAgent().getContentManager().extractContent(message);
            Concept concept = ((Action) content).getAction(); // if the content it's not action then this throws class cast exception

            if (actionClass.isInstance(concept)) {
                final T action = actionClass.cast(concept);
                try {
                    return handler.handle(action, message);
                } catch (Exception e) {
                    return MessageStatus.NOT_PROCESSED;
                }
            } else {
                return MessageStatus.NOT_PROCESSED;
            }
        } catch (Exception e) {
            return MessageStatus.NOT_PROCESSED;
        }
    }

    public Agent getAgent () {
        return agent;
    }
}
