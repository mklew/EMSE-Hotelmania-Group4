package hotelmania.group4.simulator;

import hotelmania.group4.HotelManiaAgent;
import hotelmania.group4.utils.Utils;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPAAgentManagement.FailureException;

/**
 * @author Alberth Montero <alberthm@gmail.com>
 * @since 5/6/14
 */
public class AgSimulator4 extends HotelManiaAgent {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected void setupHotelManiaAgent () {
        // Added for testing 2nd iteration Tests
        System.out.println(getLocalName()+": HAS ENTERED");

        logger.debug("setting up agent");
        try {
            // Creates its own description
            DFAgentDescription dfd = Utils.createAgentDescriptionWithNameAndType(this.getName(), SUBSCRIBETODAYEVENT);
            // Registers its description in the DF
            DFService.register(this, dfd);
            logger.info(getLocalName() + ": registered in the DF");

            // Added for testing 2nd iteration Tests
            System.out.println(getLocalName()+": registered in the DF");
            //doWait(10000); // TEST!
            // Wait 10 seconds for subscriptions
            System.out.println(getLocalName()+": Waiting subscriptions ...");

        } catch (FIPAException e) {
            e.printStackTrace();
        }


        // Agree Subscription or not?
        addBehaviour(new AchieveREResponder(this, MessageTemplate.MatchPerformative(ACLMessage.SUBSCRIBE)) {

            // Message to compare
            ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.SUBSCRIBE));

            protected ACLMessage prepareResponse(ACLMessage request) throws RefuseException {
                System.out.println(getLocalName()+": SUSCRIBE DayEvent from "+request.getSender().getName()+". Action is "+request.getContent());
                if (SUBSCRIBETODAYEVENT.equalsIgnoreCase(msg.getContent())) {
                    // We agree to perform the action. Note that in the FIPA-Request
                    // protocol the AGREE message is optional. Return null if you
                    // don't want to send it.
                    ACLMessage agree = request.createReply();
                    agree.setPerformative(ACLMessage.AGREE);
                    System.out.println(myAgent.getLocalName()+": answer sent -> "+agree.getContent());

                    return agree;
                }
                else {
                    // We refuse to perform the action
                    System.out.println("Agent "+getLocalName()+": Refuse");
                    System.out.println(myAgent.getLocalName()+": answer sent ->REFUSE");
                    throw new RefuseException("check-failed in AchieveREResponder");
                }
            }

            /*protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
                if (performAction()) {
                    System.out.println("Agent "+getLocalName()+": Action successfully performed");
                    ACLMessage inform = request.createReply();
                    inform.setPerformative(ACLMessage.INFORM);
                    return inform;
                }
                else {
                    System.out.println("Agent "+getLocalName()+": Action failed");
                    throw new FailureException("unexpected-error");
                }
            }*/
        } );

        // Cancel Subscription or not?
        addBehaviour(new CyclicBehaviour(this)
        {
            public void action()
            {
                // Waits for estimation cancel request
                ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.CANCEL));
                if (msg != null)
                {
                    // If a cancel arrives..
                    System.out.println(myAgent.getLocalName()+": CANCEL DayEvent from "+(msg.getSender()).getLocalName());
                }
                else
                {
                    // If no message arrives
                    block();
                }
            }
        });

        addBehaviour(new CyclicBehaviour(this)
        {
            public void action()
            {
                // Waits for request not understood
                ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.NOT_UNDERSTOOD));
                if (msg != null)
                {
                    // If a not understood message arrives...
                    System.out.println(myAgent.getLocalName()+": answer sent ->NOT_UNDERSTOOD");
                }
                else
                {
                    // If no message arrives
                    block();
                }
            }
        });

        // for sending the day change - ? Implement
        addBehaviour(new TickerBehaviour(this, 10000) {
            protected void onTick() {
                // perform operation Y

            }
        });
    }

}
