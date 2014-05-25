package hotelmania.group4.utils;

import hotelmania.group4.HotelManiaAgent;
import hotelmania.group4.HotelManiaAgentNames;
import jade.content.ContentElement;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 25/05/14
 */
public class SubscribeToEndOfSimulation extends SubscribeTo {

    private final OnEndOfSimulation onEndOfSimulation;

    public SubscribeToEndOfSimulation (HotelManiaAgent agent, OnEndOfSimulation onEndOfSimulation) {
        super(HotelManiaAgentNames.END_SIMULATION, agent);
        this.onEndOfSimulation = onEndOfSimulation;
    }

    @Override protected void handleContent (ContentElement content) {
        onEndOfSimulation.onEndOfSimulation();
    }
}
