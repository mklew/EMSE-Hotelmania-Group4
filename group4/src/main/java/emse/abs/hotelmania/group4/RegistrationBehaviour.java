package emse.abs.hotelmania.group4;

import com.google.common.base.Optional;
import emse.abs.hotelmania.ontology.Hotel;
import emse.abs.hotelmania.ontology.RegistrationRequest;
import jade.content.Concept;
import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 20/04/14
 */
public class RegistrationBehaviour extends CyclicBehaviour {

    Logger logger = LoggerFactory.getLogger(getClass());

    private final AgPlatform4 platform;

    public RegistrationBehaviour (AgPlatform4 a) {
        super(a);
        platform = a;
    }

    @Override
    public void action () {
        final MessageTemplate op1 = MessageTemplate.MatchLanguage(platform.getCodec().getName());
        final MessageTemplate op2 = MessageTemplate.MatchOntology(platform.getOntology().getName());
        final MessageTemplate and = MessageTemplate.and(op1,
                op2);
        MessageTemplate op3 = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
        ACLMessage msg = getAgent().receive(MessageTemplate.and(op3, and));

        final Optional<ACLMessage> optional = Optional.fromNullable(msg);

        if (optional.isPresent()) {
            final ACLMessage message = optional.get();
            final ACLMessage reply = message.createReply();
            reply.setLanguage(platform.codec.getName());
            reply.setOntology(platform.ontology.getName());

            final int messagePerformative = message.getPerformative();
            if (messagePerformative == ACLMessage.REQUEST) {
                try {
                    ContentElement content = getAgent().getContentManager().extractContent(msg);
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
            myAgent.send(reply);
        }
    }

}
