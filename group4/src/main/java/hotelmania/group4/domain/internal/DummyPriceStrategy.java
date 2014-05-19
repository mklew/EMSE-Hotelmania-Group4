package hotelmania.group4.domain.internal;

import hotelmania.group4.domain.PriceStrategy;
import hotelmania.ontology.Price;
import hotelmania.ontology.Stay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 17/05/14
 */
public class DummyPriceStrategy implements PriceStrategy {

    Logger logger = LoggerFactory.getLogger(getClass());

    // TODO implement strategy
    @Override public Price getPriceFor (Stay stay) {
        logger.info("Returning dummy Price for stay. TODO implement strategy");
        final Price price = new Price();
        price.setAmount(50);
        return price;
    }
}
