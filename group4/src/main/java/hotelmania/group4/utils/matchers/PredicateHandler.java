package hotelmania.group4.utils.matchers;

import hotelmania.group4.behaviours.MessageStatus;
import jade.content.lang.Codec;
import jade.content.onto.OntologyException;
import jade.lang.acl.ACLMessage;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 26/05/14
 */
public interface PredicateHandler<T> {
    MessageStatus handle (T predicate, ACLMessage message) throws Codec.CodecException, OntologyException;
}
