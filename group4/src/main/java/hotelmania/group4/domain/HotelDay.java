package hotelmania.group4.domain;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 17/05/14
 */
public class HotelDay {

    private final int dayNumber;

    private Set<Room> bookedRooms = new HashSet<>();

    public HotelDay (int dayNumber) {
        this.dayNumber = dayNumber;
    }

    void bookRoom (Room room) throws RoomHasBeenAlreadyBookedException {
        if (bookedRooms.contains(room)) {
            throw new RoomHasBeenAlreadyBookedException();
        } else {
            bookedRooms.add(room);
        }
    }

    boolean isRoomAvailable (Room room) {
        return bookedRooms.contains(room);
    }
}
