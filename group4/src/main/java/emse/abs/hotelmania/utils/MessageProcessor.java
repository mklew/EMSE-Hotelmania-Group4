package emse.abs.hotelmania.utils;

import emse.abs.hotelmania.behaviours.MessageStatus;
import jade.lang.acl.ACLMessage;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 21/04/14
 */
public interface MessageProcessor<T> {
    MessageStatus tryToHandle (ACLMessage message);
}
