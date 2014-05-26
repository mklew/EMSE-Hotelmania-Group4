package hotelmania.group4.domain.client;

import hotelmania.ontology.Stay;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 18/05/14
 */
public class Client {

    private final double budget;

    private final int checkInDay;

    private final int checkOutDay;

    public Client (double budget, int checkInDay, int checkOutDay) {
        this.budget = budget;
        this.checkInDay = checkInDay;
        this.checkOutDay = checkOutDay;
    }

    public double getBudget () {
        return budget;
    }

    public int getCheckInDay () {
        return checkInDay;
    }

    public int getCheckOutDay () {
        return checkOutDay;
    }

    public boolean hasToBookToday (int day) {
        return checkInDay == day + 1;
    }

    public Stay getStay () {
        final Stay stay = new Stay();
        stay.setCheckIn(checkInDay);
        stay.setCheckOut(checkOutDay);
        return stay;
    }
}
