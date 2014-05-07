package hotelmania.group4.simulator;

import hotelmania.group4.HotelManiaAgent;
import jade.core.behaviours.TickerBehaviour;

/**
 * Created by alberthmontero on 5/6/14.
 */
public abstract class EmseTicketBehaviour extends TickerBehaviour {


    public EmseTicketBehaviour (HotelManiaAgent agent) {
        super(agent,time);
    }

}
