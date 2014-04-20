package emse.abs.hotelmania.group4;

import emse.abs.hotelmania.ontology.SharedAgentsOntology;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.core.Agent;

/**
 * Base class for all hotel mania agents.
 *
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 20/04/14
 */
public abstract class HotelManiaAgent extends Agent {

    final protected Codec codec = new SLCodec();
    final protected Ontology ontology = SharedAgentsOntology.getInstance();

    @Override
    protected void setup () {
        getContentManager().registerLanguage(codec);
        getContentManager().registerOntology(ontology);
        setupHotelManiaAgent();
    }

    abstract protected void setupHotelManiaAgent ();
}
