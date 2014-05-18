package hotelmania.group4.hotel;

import com.google.inject.Inject;
import hotelmania.group4.HotelManiaAgent;
import hotelmania.group4.HotelManiaAgentNames;
import hotelmania.group4.behaviours.EmseCyclicBehaviour;
import hotelmania.group4.behaviours.MessageStatus;
import hotelmania.group4.domain.Hotel4;
import hotelmania.group4.guice.GuiceConfigurer;
import hotelmania.group4.utils.ActionMessageHandler;
import hotelmania.group4.utils.MessageMatchingChain;
import hotelmania.ontology.NumberOfClients;
import hotelmania.ontology.NumberOfClientsQueryRef;
import jade.content.lang.Codec;
import jade.content.onto.OntologyException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Arrays;
import java.util.List;

/**
 * Goal 13
 *
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 11/05/14
 */
public class RespondToNumberOfClients extends EmseCyclicBehaviour {

    @Inject
    private Hotel4 hotel4;

    public RespondToNumberOfClients (HotelManiaAgent agent) {
        super(agent);
        GuiceConfigurer.getInjector().injectMembers(this);
    }

    @Override protected List<MessageTemplate> getMessageTemplates () {
        final MessageTemplate withCodec = MessageTemplate.MatchLanguage(getHotelManiaAgent().getCodec().getName());
        final MessageTemplate withOntology = MessageTemplate.MatchOntology(getHotelManiaAgent().getOntology().getName());
        final MessageTemplate withRequestPerformative = MessageTemplate.MatchPerformative(ACLMessage.QUERY_REF);
        final MessageTemplate withProtocolName = MessageTemplate.MatchProtocol(HotelManiaAgentNames.NUMBER_OF_CLIENTS);


        return Arrays.asList(withCodec, withOntology, withRequestPerformative, withProtocolName);
    }

    @Override
    protected MessageStatus processMessage (ACLMessage message) throws Codec.CodecException, OntologyException {


        final MessageMatchingChain messageMatchingChain = new MessageMatchingChain(getHotelManiaAgent()).withActionMatcher(NumberOfClientsQueryRef.class, new ActionMessageHandler<NumberOfClientsQueryRef>() {
            @Override public MessageStatus handle (NumberOfClientsQueryRef action,
                                                   ACLMessage message) throws Codec.CodecException, OntologyException {

                final NumberOfClientsQueryRef numberOfClientsQueryRef = NumberOfClientsQueryRef.class.cast(getAgent().getContentManager().extractContent(message));

                final ACLMessage reply = getHotelManiaAgent().createReply(message);
                reply.setPerformative(ACLMessage.INFORM);
                reply.setProtocol(HotelManiaAgentNames.NUMBER_OF_CLIENTS);

                final NumberOfClients numberOfClients = new NumberOfClients();

                numberOfClients.setNum_clients(hotel4.getNumberOfClientsAtDay(numberOfClientsQueryRef.getDay()));

                getHotelManiaAgent().getContentManager().fillContent(reply, numberOfClients);
                getHotelManiaAgent().sendMessage(reply);
                return MessageStatus.PROCESSED;
            }
        });

        return messageMatchingChain.handleMessage(message);
    }
}
