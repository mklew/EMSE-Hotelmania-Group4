package hotelmania.group4.agency;

import com.google.inject.Inject;
import hotelmania.group4.behaviours.EmseCyclicBehaviour;
import hotelmania.group4.behaviours.MessageStatus;
import hotelmania.group4.domain.HotelRepositoryService;
import hotelmania.group4.guice.GuiceConfigurer;
import hotelmania.group4.utils.ActionMessageHandler;
import hotelmania.group4.utils.MessageHandler;
import hotelmania.group4.utils.MessageMatchingChain;
import hotelmania.ontology.SignContract;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Tahir on 06/05/2014.
 */
public class SignContractBehaviour extends EmseCyclicBehaviour {
    Logger logger = LoggerFactory.getLogger(getClass());

    private final AgAgency4 agency;

    @Inject
    private HotelRepositoryService hotelRepositoryService;

    public SignContractBehaviour (AgAgency4 a) {
        super(a);
        agency = a;
        GuiceConfigurer.getInjector().injectMembers(this);
    }

    @Override protected List<MessageTemplate> getMessageTemplates () {
        final MessageTemplate withCodec = MessageTemplate.MatchLanguage(agency.getCodec().getName());
        final MessageTemplate withOntology = MessageTemplate.MatchOntology(agency.getOntology().getName());
        final MessageTemplate withRequestPerformative = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);

        return Arrays.asList(withCodec, withOntology, withRequestPerformative);
    }

    @Override protected MessageStatus processMessage (final ACLMessage message) {
        final ACLMessage reply = agency.createReply(message);

        final MessageMatchingChain messageMatchingChain = new MessageMatchingChain(getAgent()).withActionMatcher(SignContract.class, new ActionMessageHandler<SignContract>() {
            @Override public MessageStatus handle (SignContract action, ACLMessage message) {

                if(action.getContract().getRecepcionist_experienced() < 3) {
                    reply.setPerformative(ACLMessage.AGREE);
                    getAgent().send(reply);
                    logger.info("Sent AGREE as a SignContract response");
                }
                if (action.getContract().getRecepcionist_experienced() > 3)
                {
                    reply.setPerformative(ACLMessage.REFUSE);
                    getAgent().send(reply);
                    logger.info("Sent REFUSE as a SignContract response");
                }
                return MessageStatus.PROCESSED;
            }
        }).withDefaultHandler(new MessageHandler() {
            @Override
            public MessageStatus handle(ACLMessage message) {
                reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
                getAgent().send(reply);
                logger.info("Sent NOT_UNDERSTOOD as a SignContract response");
                return MessageStatus.PROCESSED;
            }
        });

        return messageMatchingChain.handleMessage(message);
    }
}
