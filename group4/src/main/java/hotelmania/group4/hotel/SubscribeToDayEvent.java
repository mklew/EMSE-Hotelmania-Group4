package hotelmania.group4.hotel;

import com.google.common.base.Function;
import hotelmania.group4.HotelManiaAgentNames;
import hotelmania.group4.utils.Utils;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAException;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 08/05/14
 */ // Internal class for searching the simulator behavior
class SubscribeToDayEvent extends OneShotBehaviour {

    private final Function<DFAgentDescription[], Object> onFound;

    // Method for searching the agency
    public SubscribeToDayEvent (Agent a, Function<DFAgentDescription[], Object> f) {
        super(a);
        onFound = f;
    }

    // overriding the action() method of OneShotBehaviour
    @Override public void action () {
        DFAgentDescription dfd = Utils.createAgentDescriptionWithType(HotelManiaAgentNames.SUBSCRIBETODAYEVENT);
        try {
            final DFAgentDescription[] search = DFService.search(getAgent(), dfd);
            if (search.length == 0) {
                getAgent().doWait(5000);
                getAgent().addBehaviour(new SubscribeToDayEvent(getAgent(), onFound));
            } else {
                onFound.apply(search);
            }
        } catch (FIPAException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

}
