package hotelmania.group4.simulator;

import com.google.common.base.Function;
import hotelmania.group4.HotelManiaAgent;
import hotelmania.group4.behaviours.EmseSimpleBehaviour;
import hotelmania.group4.behaviours.MessageStatus;
import hotelmania.group4.utils.MessageHandler;
import hotelmania.group4.utils.MessageMatchingChain;
import hotelmania.group4.utils.Utils;
import hotelmania.ontology.Hotel;
import hotelmania.ontology.RegistrationRequest;
import jade.content.lang.Codec;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * @author Alberth Montero <alberthm@gmail.com>
 * @since 5/6/14
 */
public class AgSimulator4 extends HotelManiaAgent {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected void setupHotelManiaAgent () {
        System.out.println(getLocalName()+": HAS ENTERED");

        logger.debug("setting up agent");
        try {
            // Creates its own description
            DFAgentDescription dfd = Utils.createAgentDescriptionWithNameAndType(this.getName(), SUBSCRIBETODAYEVENT);
            // Registers its description in the DF
            DFService.register(this, dfd);
            logger.info(getLocalName() + ": registered in the DF");
        } catch (FIPAException e) {
            e.printStackTrace();
        }

        //addBehaviour(new RegistrationBehaviour(this));
        //addBehaviour(new SubscribeToDayEvent());




        addBehaviour(new TickerBehaviour(this, 10000) {

            protected void onTick() {
                // perform operation Y




            } } );
    }

}
