package emse.abs.hotelmania.group4;

import com.google.common.collect.ImmutableList;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.MessageTemplate;

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

    static DFAgentDescription createAgentDescriptionWithNameAndType (String name, String type) {
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
}
