package hotelmania.group4.platform;

import hotelmania.group4.HotelManiaAgent;
import hotelmania.group4.HotelManiaAgentNames;
import hotelmania.group4.RegistrationBehaviour;
import hotelmania.group4.agency.AgAgency4;
import hotelmania.group4.simulator.AgSimulator4;
import hotelmania.group4.utils.Utils;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 20/04/14
 */
public class AgPlatform4 extends HotelManiaAgent {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected void setupHotelManiaAgent () {
        System.out.println(getLocalName() + ": HAS ENTERED");

        logger.debug("setting up agent");
        try {
            // Creates its own description
            DFAgentDescription dfd = Utils.createAgentDescriptionWithNameAndType(this.getName(), REGISTRATION);
            // Registers its description in the DF
            DFService.register(this, dfd);
            logger.info(getLocalName() + ": registered in the DF");
        } catch (FIPAException e) {
            e.printStackTrace();
        }

        addBehaviour(new RegistrationBehaviour(this));

        Utils.runAgent(this, HotelManiaAgentNames.SUBSCRIBE_TO_DAY_EVENT, AgSimulator4.class);

        Utils.runAgent(this, HotelManiaAgentNames.AGENCY, AgAgency4.class);
    }
}
