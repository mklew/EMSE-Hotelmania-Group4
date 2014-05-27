package hotelmania.group4.client;

import com.google.common.collect.Sets;
import hotelmania.group4.HotelManiaAgent;
import hotelmania.group4.HotelManiaAgentNames;
import hotelmania.group4.behaviours.EmseSimpleBehaviour;
import hotelmania.group4.behaviours.MessageStatus;
import hotelmania.group4.utils.matchers.MessageHandler;
import hotelmania.group4.utils.matchers.MessageMatchingChain;
import hotelmania.ontology.HotelInformation;
import jade.content.ContentElement;
import jade.content.ContentElementList;
import jade.content.lang.Codec;
import jade.content.onto.OntologyException;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 27/05/14
 */
public class AskHotelmaniaForInformation extends EmseSimpleBehaviour {

    private boolean gotResponse = false;

    private final AID hotelmania;

    private final InformationReceived informationReceivedCallback;

    public AskHotelmaniaForInformation (HotelManiaAgent agent, AID hotelmania,
                                        InformationReceived informationReceivedCallback) {
        super(agent);
        this.hotelmania = hotelmania;
        this.informationReceivedCallback = informationReceivedCallback;
    }

    @Override protected List<MessageTemplate> getMessageTemplates () {
        final MessageTemplate withCodec = MessageTemplate.MatchLanguage(getHotelManiaAgent().getCodec().getName());
        final MessageTemplate withOntology = MessageTemplate.MatchOntology(getHotelManiaAgent().getOntology().getName());
        final MessageTemplate withProtocolName = MessageTemplate.MatchProtocol(HotelManiaAgentNames.QUERY_HOTELMANIA_INFORMATION);

        return Arrays.asList(withCodec, withOntology, withProtocolName);
    }

    @Override
    protected MessageStatus processMessage (ACLMessage message) throws Codec.CodecException, OntologyException {
        final MessageMatchingChain messageMatchingChain = new MessageMatchingChain(getHotelManiaAgent()).withMessageHandler(new MessageHandler() {
            @Override public MessageStatus handle (ACLMessage message) throws OntologyException, Codec.CodecException {
                final ContentElement contentElement = getHotelManiaAgent().getContentManager().extractContent(message);
                if (ContentElementList.class.isInstance(contentElement)) {
                    final ContentElementList contentsList = ContentElementList.class.cast(contentElement);
                    final List<ContentElement> contents = Arrays.asList(contentsList.toArray());

                    List<HotelInformation> hotelInformation = new ArrayList<>(contents.size());
                    for (ContentElement item : contents) {
                        hotelInformation.add(HotelInformation.class.cast(item));
                    }
                    informationReceivedCallback.done(Sets.newHashSet(hotelInformation));
                    gotResponse = true;
                    return MessageStatus.PROCESSED;
                } else if (HotelInformation.class.isInstance(contentElement)) {
                    final HotelInformation hotelInformation = HotelInformation.class.cast(contentElement);

                    informationReceivedCallback.done(Sets.newHashSet(hotelInformation));
                    gotResponse = true;
                    return MessageStatus.PROCESSED;

                } else {
                    return MessageStatus.NOT_PROCESSED;
                }
            }
        });

        return messageMatchingChain.handleMessage(message);
    }

    @Override public boolean done () {
        return gotResponse;
    }

    @Override protected void doAction () throws Codec.CodecException, OntologyException {
        final ACLMessage askForInformation = getHotelManiaAgent().createMessage(hotelmania, ACLMessage.QUERY_REF);
        askForInformation.setProtocol(HotelManiaAgentNames.QUERY_HOTELMANIA_INFORMATION);

        getHotelManiaAgent().sendMessage(askForInformation);
    }

    public interface InformationReceived {
        void done (Set<HotelInformation> hotelInformation);
    }
}
