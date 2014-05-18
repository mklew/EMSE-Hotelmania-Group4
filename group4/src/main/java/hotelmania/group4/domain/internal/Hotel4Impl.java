package hotelmania.group4.domain.internal;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import hotelmania.group4.domain.*;
import hotelmania.ontology.Price;
import hotelmania.ontology.Stay;

import java.util.Set;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 17/05/14
 */
public class Hotel4Impl implements PriceStrategy, Hotel4 {
    @Inject
    PriceStrategy priceStrategy;

    private final Set<Room> rooms = Sets.newHashSet(new Room(1), new Room(2), new Room(3), new Room(4), new Room(5), new Room(6));

    BookingCalendar bookingCalendar = new BookingCalendar(rooms);

    @Override public Price getPriceFor (Stay stay) throws NoRoomsAvailableException {
        if (bookingCalendar.isThereFreeRoom(stay)) {
            return priceStrategy.getPriceFor(stay);
        } else {
            throw new NoRoomsAvailableException(stay);
        }
    }

    @Override public boolean hasEmptyRooms (Stay stay) {
        return bookingCalendar.isThereFreeRoom(stay);
    }

    @Override public int getNumberOfClientsAtDay (int day) {
        return bookingCalendar.getNumberOfClientsAtDayNumber(day);
    }

    @Override public void bookRoomFor (Stay stay,
                                       Price agreedPrice) throws NoRoomsAvailableException, CurrentPriceIsHigherException {
        final Price currentPrice = priceStrategy.getPriceFor(stay);
        if (agreedPriceIsHigherOrEqualToCurrentPrice(agreedPrice, currentPrice)) {
            bookingCalendar.bookFirstFreeRoom(stay);
        } else {
            throw new CurrentPriceIsHigherException(currentPrice, agreedPrice);
        }
    }

    private boolean agreedPriceIsHigherOrEqualToCurrentPrice (Price agreedPrice, Price currentPrice) {
        return Float.compare(currentPrice.getPrice(), agreedPrice.getPrice()) <= 0;
    }
}
