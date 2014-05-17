package hotelmania.group4.domain;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.*;
import hotelmania.ontology.Stay;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 17/05/14
 */
public class BookingCalendar {

    private Map<Integer, HotelDay> hotelDayMap;

    private ReentrantLock reentrantLock = new ReentrantLock();

    private final Set<Room> allRooms;

    public BookingCalendar (Set<Room> allRooms) {
        this.hotelDayMap = new HashMap<>();
        this.allRooms = allRooms;
    }

    public boolean isThereFreeRoom (final Stay stay) {
        final Optional<Room> roomOptional = getRoomOptional(stay);
        return roomOptional.isPresent();
    }

    private HotelDay getHotelDay (Integer dayNumber) {
        if (hotelDayMap.containsKey(dayNumber)) {
            return hotelDayMap.get(dayNumber);
        } else {
            final HotelDay hotelDay = new HotelDay(dayNumber);
            hotelDayMap.put(dayNumber, hotelDay);
            return hotelDay;
        }
    }

    public Room getFreeRoom (final Stay stay) throws NoRoomsAvailableException {
        final Optional<Room> freeRoomOpt = getRoomOptional(stay);

        if (freeRoomOpt.isPresent()) {
            return freeRoomOpt.get();
        } else {
            throw new NoRoomsAvailableException(stay);
        }
    }

    private Optional<Room> getRoomOptional (final Stay stay) {
        final Optional<Room> roomOptional;

        reentrantLock.lock();
        try {
            roomOptional = (Optional<Room>) FluentIterable.from(allRooms).firstMatch(new Predicate<Room>() {
                @Override public boolean apply (final Room room) {
                    return canBookRoom(room, stay);
                }
            });
        } finally {
            reentrantLock.unlock();
        }
        return roomOptional;
    }

    private boolean canBookRoom (final Room room, Stay stay) {
        final ContiguousSet<Integer> days = getDaysOfStay(stay);

        return Iterables.all(days, new Predicate<Integer>() {
            @Override public boolean apply (Integer dayNumber) {
                HotelDay hotelDay = getHotelDay(dayNumber);
                return hotelDay.isRoomAvailable(room);
            }
        });
    }

    private ContiguousSet<Integer> getDaysOfStay (Stay stay) {
        return ContiguousSet.create(Range.closedOpen(stay.getCheckIn(), stay.getCheckOut()),
                DiscreteDomain.integers());
    }

    void bookRoomForStay (Room room, Stay stay) throws RoomHasBeenAlreadyBookedException {
        final ContiguousSet<Integer> daysOfStay = getDaysOfStay(stay);

        reentrantLock.lock();
        try {
            if (canBookRoom(room, stay)) {
                for (Integer dayOfStay : daysOfStay) {
                    final HotelDay hotelDay = getHotelDay(dayOfStay);
                    hotelDay.bookRoom(room);
                }
            } else {
                throw new RoomHasBeenAlreadyBookedException();
            }
        } finally {
            reentrantLock.unlock();
        }
    }


}
