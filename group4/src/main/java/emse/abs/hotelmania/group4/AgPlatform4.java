package emse.abs.hotelmania.group4;

import emse.abs.hotelmania.utils.Utils;
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
    }
}
