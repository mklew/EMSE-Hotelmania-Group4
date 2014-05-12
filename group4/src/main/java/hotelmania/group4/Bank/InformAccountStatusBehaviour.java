package hotelmania.group4.bank;

import com.google.inject.Inject;
import hotelmania.group4.behaviours.EmseCyclicBehaviour;
import hotelmania.group4.behaviours.EmseSimpleBehaviour;
import hotelmania.group4.behaviours.MessageStatus;
import hotelmania.group4.domain.AccountAlreadyExistsException;
import hotelmania.group4.domain.BankAccountRepository;
import hotelmania.group4.domain.internal.AccountDoesNotExistException;
import hotelmania.group4.guice.GuiceConfigurer;
import hotelmania.group4.utils.ActionMessageHandler;
import hotelmania.group4.utils.MessageHandler;
import hotelmania.group4.utils.MessageMatchingChain;
import hotelmania.ontology.Account;
import hotelmania.ontology.AccountStatusQueryRef;
import hotelmania.ontology.CreateAccountRequest;
import hotelmania.ontology.Hotel;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Created by azfar on 12/05/2014.
 */
public class InformAccountStatusBehaviour extends EmseCyclicBehaviour {
    Logger logger = LoggerFactory.getLogger(getClass());

    private final AgBank4 bank;

    @Inject
    private BankAccountRepository bankAccountRepository;

    public InformAccountStatusBehaviour (AgBank4 a) {
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

        final MessageMatchingChain messageMatchingChain = new MessageMatchingChain(getAgent()).withActionMatcher(AccountStatusQueryRef.class, new ActionMessageHandler<AccountStatusQueryRef>() {
            @Override public MessageStatus handle (AccountStatusQueryRef action, ACLMessage message) {

                final int account_ID = action.getId_account();
                try {
                    int balance = bankAccountRepository.retrieveBalance(account_ID);
                    reply.setPerformative(ACLMessage.INFORM);
                    reply.setContent((Integer.toString(balance)));
                    getAgent().send(reply);
                    logger.info("Informed hotel with the current Balance");
                } catch (AccountDoesNotExistException e) {
                    reply.setPerformative(ACLMessage.FAILURE);
                    getAgent().send(reply);
                    logger.info("FAILURE: Mentioned Account does not exist");
                }
                return MessageStatus.PROCESSED;

            }
        }).withDefaultHandler(new MessageHandler() {
            @Override
            public MessageStatus handle (ACLMessage message) {
                reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
                getAgent().send(reply);
                logger.info("Sent NOT_UNDERSTOOD as a CreateAccount response");
                return MessageStatus.PROCESSED;
            }
        });

        return messageMatchingChain.handleMessage(message);
    }
}
