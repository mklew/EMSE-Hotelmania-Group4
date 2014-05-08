package hotelmania.group4.utils;

import com.google.common.base.Function;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 08/05/14
 */
public class SearchForAgent extends OneShotBehaviour {

    private final Logger logger = LoggerFactory.getLogger(SearchForAgent.class);

    public static final int TIMEOUT = 5000;

    private final Function<DFAgentDescription[], Object> onFound;

    private final String agentName;

    public SearchForAgent (String agentName, Agent thisAgent,
                           Function<DFAgentDescription[], Object> onFoundCallback) {
        super(thisAgent);
        this.onFound = onFoundCallback;
        this.agentName = agentName;
    }

    @Override public void action () {
        DFAgentDescription dfd = Utils.createAgentDescriptionWithType(agentName);
        try {
            final DFAgentDescription[] search = DFService.search(getAgent(), dfd);
            if (search.length == 0) {
                getAgent().doWait(TIMEOUT);
                getAgent().addBehaviour(new SearchForAgent(agentName, getAgent(), onFound));
            } else {
                onFound.apply(search);
            }
        } catch (FIPAException e) {
            logger.debug("Exception", e);
        }
    }

}
