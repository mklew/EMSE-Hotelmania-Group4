package hotelmania.group4.simulator;

import com.google.inject.Inject;
import hotelmania.group4.behaviours.EmseCyclicBehaviour;
import hotelmania.group4.behaviours.MessageStatus;
import hotelmania.group4.domain.HotelAlreadyRegisteredException;
import hotelmania.group4.domain.HotelRepositoryService;
import hotelmania.group4.guice.GuiceConfigurer;
import hotelmania.group4.platform.AgPlatform4;
import hotelmania.group4.utils.ActionMessageHandler;
import hotelmania.group4.utils.MessageHandler;
import hotelmania.group4.utils.MessageMatchingChain;
import hotelmania.ontology.Hotel;
import hotelmania.ontology.RegistrationRequest;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * @author Alberth Montero <alberthm@gmail.com>
 * @since 20/04/14
 */
public class SubscribeToDayEvent extends TicketBehaviour {

            protected void onTick() {
                // perform operation Y
            } }
