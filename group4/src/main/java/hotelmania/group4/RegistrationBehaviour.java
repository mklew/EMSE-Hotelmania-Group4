package hotelmania.group4;

import com.google.inject.Inject;
import emse.abs.hotelmania.ontology.Hotel;
import emse.abs.hotelmania.ontology.RegistrationRequest;
import hotelmania.group4.behaviours.EmseCyclicBehaviour;
import hotelmania.group4.behaviours.MessageStatus;
import hotelmania.group4.domain.HotelAlreadyRegisteredException;
import hotelmania.group4.domain.HotelRepositoryService;
import hotelmania.group4.guice.GuiceConfigurer;
import hotelmania.group4.platform.AgPlatform4;
import hotelmania.group4.utils.ActionMessageHandler;
import hotelmania.group4.utils.MessageHandler;
import hotelmania.group4.utils.MessageMatchingChain;
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

    @Inject
    private HotelRepositoryService hotelRepositoryService;

    public RegistrationBehaviour (AgPlatform4 a) {
        platform = a;
        GuiceConfigurer.getInjector().injectMembers(this);
    }

    @Override protected List<MessageTemplate> getMessageTemplates () {
        final MessageTemplate withCodec = MessageTemplate.MatchLanguage(platform.getCodec().getName());
        final MessageTemplate withOntology = MessageTemplate.MatchOntology(platform.getOntology().getName());
        final MessageTemplate withRequestPerformative = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);

        return Arrays.asList(withCodec, withOntology, withRequestPerformative);
    }

    @Override protected MessageStatus processMessage (final ACLMessage message) {
        final ACLMessage reply = platform.createReply(message);

        final MessageMatchingChain messageMatchingChain = new MessageMatchingChain(getAgent()).withActionMatcher(RegistrationRequest.class, new ActionMessageHandler<RegistrationRequest>() {
            @Override public MessageStatus handle (RegistrationRequest action, ACLMessage message) {
                final Hotel hotel = action.getHotel();
                try {
                    hotelRepositoryService.registerHotel(hotel);
                    reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                } catch (HotelAlreadyRegisteredException e) {
                    reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                }
                getAgent().send(reply);
                return MessageStatus.PROCESSED;
            }
        }).withDefaultHandler(new MessageHandler() {
            @Override public MessageStatus handle (ACLMessage message) {
                reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
                getAgent().send(reply);
                return MessageStatus.PROCESSED;
            }
        });

        return messageMatchingChain.handleMessage(message);
    }

}
