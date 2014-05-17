package hotelmania.group4.utils;

import hotelmania.group4.behaviours.MessageStatus;
import jade.content.lang.Codec;
import jade.content.onto.OntologyException;
import jade.content.onto.UngroundedException;
import jade.lang.acl.ACLMessage;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 21/04/14
 */
public interface MessageHandler {
    MessageStatus handle (ACLMessage message) throws OntologyException, Codec.CodecException;
}
