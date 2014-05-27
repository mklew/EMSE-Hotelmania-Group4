package hotelmania.group4.platform;

import com.google.inject.Inject;
import hotelmania.group4.HotelManiaAgentNames;
import hotelmania.group4.behaviours.EmseCyclicBehaviour;
import hotelmania.group4.behaviours.MessageStatus;
import hotelmania.group4.domain.HotelRepositoryService;
import hotelmania.group4.guice.GuiceConfigurer;
import hotelmania.ontology.HotelInformation;
import jade.content.ContentElementList;
import jade.content.lang.Codec;
import jade.content.onto.OntologyException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 17/05/14
 */
public class QueryInformationBehaviour extends EmseCyclicBehaviour {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Inject
    HotelRepositoryService hotelRepositoryService;

    public QueryInformationBehaviour (AgPlatform4 agPlatform4) {
        super(agPlatform4);
        GuiceConfigurer.getInjector().injectMembers(this);
    }

    @Override protected List<MessageTemplate> getMessageTemplates () {
        final MessageTemplate withCodec = MessageTemplate.MatchLanguage(getHotelManiaAgent().getCodec().getName());
        final MessageTemplate withOntology = MessageTemplate.MatchOntology(getHotelManiaAgent().getOntology().getName());
        final MessageTemplate withRequestPerformative = MessageTemplate.MatchPerformative(ACLMessage.QUERY_REF);
        final MessageTemplate withProtocolName = MessageTemplate.MatchProtocol(HotelManiaAgentNames.QUERY_HOTELMANIA_INFORMATION);

        return Arrays.asList(withCodec, withOntology, withRequestPerformative, withProtocolName);
    }

    @Override
    protected MessageStatus processMessage (ACLMessage message) throws Codec.CodecException, OntologyException {
        final ACLMessage reply = getHotelManiaAgent().createReply(message);
        reply.setPerformative(ACLMessage.INFORM);
        reply.setProtocol(HotelManiaAgentNames.QUERY_HOTELMANIA_INFORMATION);
        ContentElementList listOfHotels = new ContentElementList();

        final Set<HotelInformation> hotelInformations = hotelRepositoryService.getHotelInformation();
        for (HotelInformation hotelInformation : hotelInformations) {
            listOfHotels.add(hotelInformation);
        }

        getAgent().getContentManager().fillContent(reply, listOfHotels);
        getHotelManiaAgent().sendMessage(reply);
        logger.info("INFORM Query Hotelmania Information sent");
        return MessageStatus.PROCESSED;
    }
}
