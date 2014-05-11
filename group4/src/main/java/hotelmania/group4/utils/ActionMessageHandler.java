package hotelmania.group4.utils;

import hotelmania.group4.behaviours.MessageStatus;
import jade.content.lang.Codec;
import jade.content.onto.OntologyException;
import jade.lang.acl.ACLMessage;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 21/04/14
 */
public interface ActionMessageHandler<T> {
    MessageStatus handle (T action, ACLMessage message) throws Codec.CodecException, OntologyException;
}
