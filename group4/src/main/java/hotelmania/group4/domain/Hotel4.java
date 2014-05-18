package hotelmania.group4.domain;

import hotelmania.ontology.Price;
import hotelmania.ontology.Stay;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 17/05/14
 */
public interface Hotel4 {
    Price getPriceFor (Stay stay) throws NoRoomsAvailableException;

    boolean hasEmptyRooms (Stay stay);

    int getNumberOfClientsAtDay (int day);
}
