package hotelmania.group4.utils;

import hotelmania.group4.behaviours.MessageStatus;
import jade.lang.acl.ACLMessage;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 21/04/14
 */
public interface MessageProcessor<T> {
    MessageStatus tryToHandle (ACLMessage message);
}
