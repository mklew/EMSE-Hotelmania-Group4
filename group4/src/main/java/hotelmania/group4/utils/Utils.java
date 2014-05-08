package hotelmania.group4.utils;

import com.google.common.collect.ImmutableList;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;

import java.util.Iterator;
import java.util.List;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 20/04/14
 */
public class Utils {

    public static DFAgentDescription createAgentDescriptionWithType (String type) {
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType(type);
        dfd.addServices(sd);
        return dfd;
    }

    public static DFAgentDescription createAgentDescriptionWithNameAndType (String name, String type) {
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setName(name);
        sd.setType(type);
        dfd.addServices(sd);
        return dfd;
    }

    public static MessageTemplate messageTemplateConjunction (List<MessageTemplate> messageTemplates) {
        if (messageTemplates.size() == 1) {
            return messageTemplates.iterator().next();
        } else {
            final Iterator<MessageTemplate> iterator = messageTemplates.iterator();
            final MessageTemplate first = iterator.next();
            final ImmutableList<MessageTemplate> rest = ImmutableList.copyOf(iterator);
            return MessageTemplate.and(first, messageTemplateConjunction(rest));
        }
    }

    public static <T> AgentController runAgent (Agent agent, String name, Class<T> agentClass) {
        try {
            final AgentController newAgent = agent.getContainerController().getPlatformController().createNewAgent(name, agentClass.getName(), new Object[]{});
            newAgent.start();
            return newAgent;
        } catch (ControllerException e) {
            throw new RuntimeException(e);
        }
    }
}
