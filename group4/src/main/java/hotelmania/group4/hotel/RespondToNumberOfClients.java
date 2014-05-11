package hotelmania.group4.hotel;

import hotelmania.group4.HotelManiaAgent;
import hotelmania.group4.HotelManiaAgentNames;
import hotelmania.group4.behaviours.EmseCyclicBehaviour;
import hotelmania.group4.behaviours.MessageStatus;
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
    public RespondToNumberOfClients (HotelManiaAgent agent) {
        super(agent);
    }

    @Override protected List<MessageTemplate> getMessageTemplates () {
        final MessageTemplate withCodec = MessageTemplate.MatchLanguage(getHotelManiaAgent().getCodec().getName());
        final MessageTemplate withOntology = MessageTemplate.MatchOntology(getHotelManiaAgent().getOntology().getName());
        final MessageTemplate withRequestPerformative = MessageTemplate.MatchPerformative(ACLMessage.QUERY_REF);
        final MessageTemplate withProtocolName = MessageTemplate.MatchProtocol(HotelManiaAgentNames.NUMBER_OF_CLIENTS);


        return Arrays.asList(withCodec, withOntology, withRequestPerformative, withProtocolName);
    }

    @Override protected MessageStatus processMessage (ACLMessage message) {


        final MessageMatchingChain messageMatchingChain = new MessageMatchingChain(getHotelManiaAgent()).withActionMatcher(NumberOfClientsQueryRef.class, new ActionMessageHandler<NumberOfClientsQueryRef>() {
            @Override public MessageStatus handle (NumberOfClientsQueryRef action,
                                                   ACLMessage message) throws Codec.CodecException, OntologyException {

                // TODO why is there an NumberOfClientsQueryRef??

                final ACLMessage reply = message.createReply();
                reply.setPerformative(ACLMessage.INFORM);
                final NumberOfClients numberOfClients = new NumberOfClients();
                numberOfClients.setNum_clients(0); // TODO actually calculate number of clients
                getHotelManiaAgent().getContentManager().fillContent(reply, numberOfClients);

                getHotelManiaAgent().sendMessage(reply);

                return MessageStatus.PROCESSED;
            }
        });

        return messageMatchingChain.handleMessage(message);
    }
}
