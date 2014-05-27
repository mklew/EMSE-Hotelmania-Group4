package hotelmania.group4.utils.matchers;

import hotelmania.group4.behaviours.MessageStatus;
import jade.content.ContentElement;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 26/05/14
 */
public class PredicateMessageMatcher<T> implements MessageProcessor<T> {

    final Class<T> clazz;

    private final Agent agent;

    private final PredicateHandler<T> handler;

    Logger logger = LoggerFactory.getLogger(getClass());


    public PredicateMessageMatcher (Class<T> clazz, Agent agent, PredicateHandler<T> handler) {
        this.clazz = clazz;
        this.agent = agent;
        this.handler = handler;
    }

    @Override public MessageStatus tryToHandle (ACLMessage message) {
        final int performative = message.getPerformative();
        if (performative == ACLMessage.INFORM || performative == ACLMessage.QUERY_REF) {
            try {
                ContentElement content = agent.getContentManager().extractContent(message);
                if (clazz.isInstance(content)) {
                    final T action = clazz.cast(content);
                    try {
                        return handler.handle(action, message);
                    } catch (Exception e) {
                        logger.debug("Error while handling message", e);
                        return MessageStatus.NOT_PROCESSED;
                    }
                } else {
                    return MessageStatus.NOT_PROCESSED;
                }
            } catch (Exception e) {
                logger.debug("Error", e);
                return MessageStatus.NOT_PROCESSED;
            }
        } else {
            return MessageStatus.NOT_PROCESSED;
        }
    }
}
