package hotelmania.group4.Bank;

import com.google.inject.Inject;
import hotelmania.group4.behaviours.EmseCyclicBehaviour;
import hotelmania.group4.behaviours.MessageStatus;
import hotelmania.group4.domain.AccountAlreadyExistsException;
import hotelmania.group4.domain.BankAccountRepository;
import hotelmania.group4.domain.HotelAlreadyRegisteredException;
import hotelmania.group4.domain.HotelRepositoryService;
import hotelmania.group4.guice.GuiceConfigurer;
import hotelmania.group4.utils.ActionMessageHandler;
import hotelmania.group4.utils.MessageHandler;
import hotelmania.group4.utils.MessageMatchingChain;
import hotelmania.ontology.CreateAccountRequest;
import hotelmania.ontology.Hotel;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Tahir on 09/05/2014.
 */
public class CreateAccountBehaviour extends EmseCyclicBehaviour {
    Logger logger = LoggerFactory.getLogger(getClass());

    private final AgBank4 bank;

    @Inject
    private BankAccountRepository bankAccountRepository;

    public CreateAccountBehaviour (AgBank4 a) {
        super(a);
        bank = a;
        GuiceConfigurer.getInjector().injectMembers(this);
    }

    @Override protected List<MessageTemplate> getMessageTemplates () {
        final MessageTemplate withCodec = MessageTemplate.MatchLanguage(bank.getCodec().getName());
        final MessageTemplate withOntology = MessageTemplate.MatchOntology(bank.getOntology().getName());
        final MessageTemplate withRequestPerformative = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);

        return Arrays.asList(withCodec, withOntology, withRequestPerformative);
    }

    @Override protected MessageStatus processMessage (final ACLMessage message) {
        final ACLMessage reply = bank.createReply(message);

        final MessageMatchingChain messageMatchingChain = new MessageMatchingChain(getAgent()).withActionMatcher(CreateAccountRequest.class, new ActionMessageHandler<CreateAccountRequest>() {
            @Override public MessageStatus handle (CreateAccountRequest action, ACLMessage message) {

                final Hotel hotel = action.getHotel();
                try {
                    bankAccountRepository.createAccount(hotel);
                    reply.setPerformative(ACLMessage.AGREE);
                    getAgent().send(reply);
                    logger.info("Sent AGREE as a CreateAccount response");
                } catch (AccountAlreadyExistsException e) {
                    reply.setPerformative(ACLMessage.REFUSE);
                    getAgent().send(reply);
                    logger.info("Sent REFUSE as a CreateAccount response");
                }
                return MessageStatus.PROCESSED;

            }
        }).withDefaultHandler(new MessageHandler() {
            @Override
            public MessageStatus handle(ACLMessage message) {
                reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
                getAgent().send(reply);
                logger.info("Sent NOT_UNDERSTOOD as a CreateAccount response");
                return MessageStatus.PROCESSED;
            }
        });

        return messageMatchingChain.handleMessage(message);
    }
}
