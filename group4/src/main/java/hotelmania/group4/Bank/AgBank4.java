package hotelmania.group4.bank;

import hotelmania.group4.HotelManiaAgent;
import hotelmania.group4.utils.Utils;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Tahir on 09/05/2014.
 */
public class AgBank4 extends HotelManiaAgent {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected void setupHotelManiaAgent () {
        System.out.println(getLocalName() + ": HAS ENTERED");

        logger.debug("setting up agent");

        try {
            // Creates its own description
            DFAgentDescription dfd = Utils.createAgentDescriptionWithNameAndType(this.getName(), CREATE_ACCOUNT, ACCOUNT_STATUS);

            // Registers its description in the DF
            DFService.register(this, dfd);
            logger.info(getLocalName() + ": registered in the DF");
        } catch (FIPAException e) {
            e.printStackTrace();
        }

        addBehaviour(new CreateAccountBehaviour(this));
        addBehaviour(new InformAccountStatusBehaviour(this));

    }
}
