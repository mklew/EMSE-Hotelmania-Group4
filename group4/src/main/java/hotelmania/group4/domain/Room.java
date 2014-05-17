package hotelmania.group4.domain;

import com.google.common.base.Preconditions;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 17/05/14
 */
public class Room {

    private final int roomNumber;

    public Room (int roomNumber) {
        Preconditions.checkArgument(roomNumber >= 0 && roomNumber <= 6);
        this.roomNumber = roomNumber;
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Room room = (Room) o;

        if (roomNumber != room.roomNumber) return false;

        return true;
    }

    @Override
    public int hashCode () {
        return roomNumber;
    }
}
