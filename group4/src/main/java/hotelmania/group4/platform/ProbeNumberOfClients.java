package hotelmania.group4.platform;

import com.google.inject.Inject;
import hotelmania.group4.HotelManiaAgent;
import hotelmania.group4.HotelManiaAgentNames;
import hotelmania.group4.domain.HotelManiaCalendar;
import hotelmania.group4.domain.HotelRepositoryService;
import hotelmania.group4.guice.GuiceConfigurer;
import hotelmania.ontology.Hotel;
import hotelmania.ontology.NumberOfClientsQueryRef;
import jade.content.lang.Codec;
import jade.content.onto.OntologyException;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 12/05/14
 */
public class ProbeNumberOfClients extends TickerBehaviour {

    private final HotelManiaAgent agent;

    Logger logger = LoggerFactory.getLogger(getClass());

    @Inject
    HotelRepositoryService hotelRepositoryService;

    @Inject
    HotelManiaCalendar calendar;

    public ProbeNumberOfClients (HotelManiaAgent agent) {
        super(agent, 30000);
        this.agent = agent;
        GuiceConfigurer.getInjector().injectMembers(this);
    }

    @Override protected void onTick () {
        logger.debug("Probing number of clients");
        final Set<Hotel> hotels = hotelRepositoryService.getHotels();

        for (Hotel hotel : hotels) {
            final ACLMessage aclMessage = agent.createMessage(hotel.getHotelAgent(), ACLMessage.QUERY_REF);
            aclMessage.setProtocol(HotelManiaAgentNames.NUMBER_OF_CLIENTS);

            NumberOfClientsQueryRef numberOfClientsQueryRef = new NumberOfClientsQueryRef();
            numberOfClientsQueryRef.setDay(calendar.today().getDay());

            try {
                agent.getContentManager().fillContent(aclMessage, numberOfClientsQueryRef);
            } catch (Codec.CodecException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (OntologyException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            agent.sendMessage(aclMessage);
        }
    }
}
