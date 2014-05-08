package hotelmania.group4.utils;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import jade.content.lang.Codec;
import jade.content.onto.OntologyException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 08/05/14
 */
public abstract class ProcessDescription<T> implements Function<DFAgentDescription[], Object> {

    private final static Logger logger = LoggerFactory.getLogger(ProcessDescription.class);

    @Override public Object apply (DFAgentDescription[] dfAgentDescriptions) {
        final Optional<Object> found;
        try {
            found = found(dfAgentDescriptions);
        } catch (Codec.CodecException | OntologyException e) {
            logger.debug("Exception", e);
            throw new RuntimeException(e);
        }
        return found.orNull();
    }

    public abstract <T> Optional<T> found (
            DFAgentDescription[] dfAgentDescriptions) throws Codec.CodecException, OntologyException;
}
