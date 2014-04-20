package emse.abs.hotelmania.group4;

import emse.abs.hotelmania.ontology.SharedAgentsOntology;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

/**
 * Base class for all hotel mania agents.
 *
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 20/04/14
 */
public abstract class HotelManiaAgent extends Agent implements HotelManiaAgentNames {

    final private Codec codec = new SLCodec();
    final private Ontology ontology = SharedAgentsOntology.getInstance();

    @Override
    protected void setup () {
        getContentManager().registerLanguage(codec);
        getContentManager().registerOntology(ontology);
        setupHotelManiaAgent();
    }

    abstract protected void setupHotelManiaAgent ();

    public Codec getCodec () {
        return codec;
    }

    public Ontology getOntology () {
        return ontology;
    }

    protected ACLMessage createMessage (AID receiver, int performative) {
        ACLMessage msg = new ACLMessage(performative);
        msg.addReceiver(receiver);
        msg.setLanguage(codec.getName());
        msg.setOntology(ontology.getName());
        return msg;
    }

    public ACLMessage createReply (ACLMessage message) {
        final ACLMessage reply = message.createReply();
        reply.setLanguage(codec.getName());
        reply.setOntology(ontology.getName());
        return reply;
    }
}
