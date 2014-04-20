package emse.abs.hotelmania.group4;

import emse.abs.hotelmania.behaviours.EmseCyclicBehaviour;
import emse.abs.hotelmania.behaviours.MessageStatus;
import emse.abs.hotelmania.ontology.Hotel;
import emse.abs.hotelmania.ontology.RegistrationRequest;
import jade.content.Concept;
import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 20/04/14
 */
public class RegistrationBehaviour extends EmseCyclicBehaviour {

    Logger logger = LoggerFactory.getLogger(getClass());

    private final AgPlatform4 platform;

    public RegistrationBehaviour (AgPlatform4 a) {
        platform = a;
    }

    @Override protected List<MessageTemplate> getMessageTemplates () {
        final MessageTemplate withCodec = MessageTemplate.MatchLanguage(platform.getCodec().getName());
        final MessageTemplate withOntology = MessageTemplate.MatchOntology(platform.getOntology().getName());
        final MessageTemplate withRequestPerformative = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);

        return Arrays.asList(withCodec, withOntology, withRequestPerformative);
    }

    @Override protected MessageStatus processMessage (final ACLMessage message) {
        final ACLMessage reply = platform.createReply(message);
        final int messagePerformative = message.getPerformative();
        if (messagePerformative == ACLMessage.REQUEST) {
            try {
                ContentElement content = getAgent().getContentManager().extractContent(message);
                Concept action = ((Action) content).getAction();

                if (action instanceof RegistrationRequest) {
                    final RegistrationRequest registrationRequest = RegistrationRequest.class.cast(action);
                    final Hotel hotel = registrationRequest.getHotel();

                    try {
                        platform.registerHotel(hotel);
                        reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                    } catch (HotelAlreadyRegisteredException e) {
                        reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                    }
                }
            } catch (Codec.CodecException e) {
                e.printStackTrace();
            } catch (OntologyException e) {
                e.printStackTrace();
            }
        } else {
            logger.debug("unexpected message", message.toString());
            reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
        }
        getAgent().send(reply);
        return MessageStatus.PROCESSED;
    }

}
