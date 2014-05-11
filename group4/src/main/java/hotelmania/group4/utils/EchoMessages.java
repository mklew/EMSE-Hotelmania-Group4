package hotelmania.group4.utils;

import hotelmania.group4.HotelManiaAgent;
import hotelmania.group4.behaviours.EmseCyclicBehaviour;
import hotelmania.group4.behaviours.MessageStatus;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 12/05/14
 */
public class EchoMessages extends EmseCyclicBehaviour {

    Logger logger = LoggerFactory.getLogger(getClass());

    public EchoMessages (HotelManiaAgent agent) {
        super(agent);
    }

    @Override protected List<MessageTemplate> getMessageTemplates () {
        return Collections.emptyList();
    }

    @Override protected MessageStatus processMessage (ACLMessage message) {
        logger.debug("Got message: {}", message.toString());
        return MessageStatus.PROCESSED;
    }
}
