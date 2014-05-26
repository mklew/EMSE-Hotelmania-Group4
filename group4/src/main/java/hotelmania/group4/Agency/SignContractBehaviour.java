package hotelmania.group4.agency;

import com.google.inject.Inject;
import hotelmania.group4.behaviours.EmseCyclicBehaviour;
import hotelmania.group4.behaviours.MessageStatus;
import hotelmania.group4.domain.*;
import hotelmania.group4.domain.internal.HotelHasNotBeenRegistered;
import hotelmania.group4.guice.GuiceConfigurer;
import hotelmania.group4.utils.ActionMessageHandler;
import hotelmania.group4.utils.MessageHandler;
import hotelmania.group4.utils.MessageMatchingChain;
import hotelmania.ontology.Contract;
import hotelmania.ontology.DayEvent;
import hotelmania.ontology.SignContract;
import jade.content.lang.Codec;
import jade.content.onto.OntologyException;
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

    @Inject
    private BankAccountRepository bankAccountRepository;

    @Inject
    HotelManiaCalendar hotelManiaCalendar;

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

    @Override
    protected MessageStatus processMessage (final ACLMessage message) throws Codec.CodecException, OntologyException {


        final MessageMatchingChain messageMatchingChain = new MessageMatchingChain(getAgent()).withActionMatcher(SignContract.class, new ActionMessageHandler<SignContract>() {
            @Override public MessageStatus handle (SignContract action, ACLMessage message) {
                final ACLMessage agreeOrRefuse = agency.createReply(message);
                final DayEvent today = hotelManiaCalendar.today();

                final Contract contract = action.getContract();

                if (isForTomorrow(contract, today)) {
                    try {
                        final HotelData hotelData = hotelRepositoryService.getHotel(action.getHotel());
                        agreeOrRefuse.setPerformative(ACLMessage.AGREE);
                        getAgent().send(agreeOrRefuse);

                        final float contractValue = ContractValueCalculator.calculateContractValue(contract);
                        bankAccountRepository.chargeHotel(hotelData.getHotel(), contractValue);
                        hotelData.saveContract(contract);

                        final ACLMessage inform = agency.createReply(message);
                        inform.setPerformative(ACLMessage.INFORM);
                        getHotelManiaAgent().sendMessage(inform);

                    } catch (HotelHasNotBeenRegistered hotelHasNotBeenRegistered) {
                        agreeOrRefuse.setPerformative(ACLMessage.REFUSE);
                        getAgent().send(agreeOrRefuse);
                        logger.info("Sending SIGN CONTRACT REFUSE to agent {} because hotel '{}' has not been registered", message.getSender().getName(), action.getHotel().getHotel_name());
                    }

                } else {
                    agreeOrRefuse.setPerformative(ACLMessage.REFUSE);
                    getAgent().send(agreeOrRefuse);
                    logger.info("Sending SIGN CONTRACT REFUSE to agent {} because today is {} and contract is for day {} which is not tomorrow", message.getSender().getName(), today.getDay(), contract.getDay());
                }
                return MessageStatus.PROCESSED;
            }
        }).withDefaultHandler(new MessageHandler() {
            @Override
            public MessageStatus handle (ACLMessage message) {
                final ACLMessage notUnderstoodMsg = agency.createReply(message);
                notUnderstoodMsg.setPerformative(ACLMessage.NOT_UNDERSTOOD);
                getAgent().send(notUnderstoodMsg);
                logger.info("Sent NOT_UNDERSTOOD as a SignContract response");
                return MessageStatus.PROCESSED;
            }
        });

        return messageMatchingChain.handleMessage(message);
    }

    private boolean isForTomorrow (Contract contract, DayEvent today) {
        return today.getDay() + 1 == contract.getDay();
    }
}
