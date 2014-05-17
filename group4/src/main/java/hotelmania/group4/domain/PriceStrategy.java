package hotelmania.group4.domain;

import hotelmania.ontology.Price;
import hotelmania.ontology.Stay;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 17/05/14
 */
public interface PriceStrategy {

    Price getPriceFor(Stay stay) throws NoRoomsAvailableException;
}
