package hotelmania.group4.domain;

import com.google.common.collect.Sets;
import hotelmania.ontology.Stay;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Set;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 18/05/14
 */
@Test
public class BookingCalendarTest {

    private final static Room room1 = new Room(1);

    private final static Room room2 = new Room(2);

    private final Set<Room> rooms = Sets.newHashSet(room1, room2);

    private BookingCalendar bookingCalendar;

    @BeforeMethod(firstTimeOnly = true)
    public void before () {
        bookingCalendar = new BookingCalendar(rooms);
    }

    @Test
    public void should_find_free_room_when_there_are_not_booked_rooms () {
        final Stay stay = new Stay();
        stay.setCheckIn(2);
        stay.setCheckOut(5);
        assertThat(bookingCalendar.isThereFreeRoom(stay)).isTrue();
    }

    @Test
    public void booked_room_should_not_be_found_as_free () {
        // given
        final Stay stay = new Stay();
        stay.setCheckIn(2);
        stay.setCheckOut(5);

        for (Room room : rooms) {
            try {
                bookingCalendar.bookRoomForStay(room, stay);
            } catch (RoomHasBeenAlreadyBookedException e) {
                Assert.fail();
            }
        }

        // when
        final boolean thereFreeRoom = bookingCalendar.isThereFreeRoom(stay);

        // then
        assertThat(thereFreeRoom).isFalse();
    }

    @Test
    public void should_find_one_free_room_left () throws RoomHasBeenAlreadyBookedException, NoRoomsAvailableException {
        // given
        final Stay stay = new Stay();
        stay.setCheckIn(2);
        stay.setCheckOut(5);

        bookingCalendar.bookRoomForStay(room1, stay);

        // when
        final Room freeRoom = bookingCalendar.getFreeRoom(stay);

        assertThat(freeRoom).isEqualTo(room2);
    }

    @Test
    public void should_be_able_to_book_room_at_checkout_day () {
        // given
        final Stay stay1 = new Stay();
        stay1.setCheckIn(2);
        stay1.setCheckOut(5);

        final Stay stay2 = new Stay();
        stay2.setCheckIn(5);
        stay2.setCheckOut(9);

        try {
            bookingCalendar.bookRoomForStay(room1, stay1);
            bookingCalendar.bookRoomForStay(room1, stay2);
        } catch (RoomHasBeenAlreadyBookedException e) {
            Assert.fail();
        }
    }

    @Test(expectedExceptions = RoomHasBeenAlreadyBookedException.class)
    public void room_should_not_be_able_to_be_booked_when_it_overlaps_with_another_stay () throws RoomHasBeenAlreadyBookedException {
        final Stay stay1 = new Stay();
        stay1.setCheckIn(3);
        stay1.setCheckOut(6);

        final Stay overlappingStay = new Stay();
        overlappingStay.setCheckIn(1);
        overlappingStay.setCheckOut(10);

        try {
            bookingCalendar.bookRoomForStay(room1, stay1);
        } catch (RoomHasBeenAlreadyBookedException e) {
            Assert.fail();
        }

        bookingCalendar.bookRoomForStay(room1, overlappingStay);
    }

    /**
     * This test works but is disabled due to Maven Surefire BUG http://jira.codehaus.org/browse/SUREFIRE-654
     * @throws RoomHasBeenAlreadyBookedException
     */

    @Test(invocationCount = 100, threadPoolSize = 5, successPercentage = 1, timeOut = 500, enabled = false)
    public void only_one_should_be_able_to_book_room_at_same_time() throws RoomHasBeenAlreadyBookedException {
        final Stay stay1 = new Stay();
        stay1.setCheckIn(3);
        stay1.setCheckOut(6);

        bookingCalendar.bookRoomForStay(room1, stay1);
    }

}
