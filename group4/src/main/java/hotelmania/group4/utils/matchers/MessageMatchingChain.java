package hotelmania.group4.utils.matchers;


import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import hotelmania.group4.behaviours.MessageStatus;
import jade.content.lang.Codec;
import jade.content.onto.OntologyException;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 21/04/14
 */
public class MessageMatchingChain {
    private final Agent agent;

    private List<ActionMatcher<?>> actionMatchers = new ArrayList<>();

    private List<MessageHandler> messageProcessors = new ArrayList<>();

    private List<PredicateMessageMatcher> predicateMessageMatchers = new ArrayList<>();

    private Optional<MessageHandler> defaultHandler = Optional.absent();

    public MessageMatchingChain (Agent agent) {
        this.agent = agent;
    }

    public <T> MessageMatchingChain withActionMatcher (Class<T> actionClass, ActionMessageHandler<T> handler) {
        Preconditions.checkNotNull(handler);
        actionMatchers.add(new ActionMatcher<T>(actionClass, agent, handler));
        return this;
    }

    public <T> MessageMatchingChain withPredicateMatcher (Class<T> clazz, PredicateHandler<T> handler) {
        Preconditions.checkNotNull(handler);
        predicateMessageMatchers.add(new PredicateMessageMatcher<T>(clazz, agent, handler));
        return this;
    }

    public MessageMatchingChain withDefaultHandler (MessageHandler handler) {
        Preconditions.checkNotNull(handler);
        defaultHandler = Optional.of(handler);
        return this;
    }

    public MessageMatchingChain withMessageHandler (MessageHandler messageHandler) {
        Preconditions.checkNotNull(messageHandler);
        messageProcessors.add(messageHandler);
        return this;
    }

    public MessageStatus handleMessage (ACLMessage message) throws Codec.CodecException, OntologyException {
        MessageStatus messageStatus = MessageStatus.NOT_PROCESSED;
        for (ActionMatcher<?> actionMatcher : actionMatchers) {
            messageStatus = actionMatcher.tryToHandle(message);
            if (messageStatus.equals(MessageStatus.PROCESSED)) {
                break;
            }
        }

        if (messageStatus.equals(MessageStatus.NOT_PROCESSED)) {
            for (PredicateMessageMatcher<?> processor : predicateMessageMatchers) {
                messageStatus = processor.tryToHandle(message);
                if (messageStatus.equals(MessageStatus.PROCESSED)) {
                    break;
                }
            }
        }

        if (messageStatus.equals(MessageStatus.NOT_PROCESSED)) {
            for (MessageHandler processor : messageProcessors) {
                messageStatus = processor.handle(message);
                if (messageStatus.equals(MessageStatus.PROCESSED)) {
                    break;
                }
            }
        }

        if (messageStatus.equals(MessageStatus.NOT_PROCESSED) && defaultHandler.isPresent()) {
            messageStatus = defaultHandler.get().handle(message);
        }

        return messageStatus;
    }

}
