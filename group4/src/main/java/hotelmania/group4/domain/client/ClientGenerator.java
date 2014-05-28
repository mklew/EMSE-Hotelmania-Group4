package hotelmania.group4.domain.client;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import hotelmania.group4.settings.Settings;

import java.util.Random;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 18/05/14
 */
public class ClientGenerator {

    private Settings settings;

    @Inject
    public ClientGenerator (Settings settings) {
        this.settings = settings;
    }

    public Client generateClient (int currentDay) {

        int Min = (int) (settings.getClientBudget() - settings.getBudgetVariance());
        int Max = (int) (settings.getClientBudget() + settings.getBudgetVariance());
        final int randomBudget = Min + (int) (Math.random() * ((Max - Min) + 1));

        final int simulationDays = settings.getSimulationDays();

        final int daysLeft = simulationDays - currentDay;
        if(daysLeft == 0) {
            throw new RuntimeException("Cannot create client on last day of simulation");
        }
        else {
            Preconditions.checkArgument(daysLeft > 0, "There should be days left. Current day is {} and days left {}", currentDay, daysLeft);

            if(daysLeft == 1) {
                return new Client(randomBudget, currentDay, simulationDays);
            }
            else {
                final Random random = new Random();
                final int daysToCheckIn = random.nextInt(daysLeft);

                final int checkInDay = Math.min(simulationDays - 1, currentDay + daysToCheckIn);

                final int daysLeftAfterCheckIn = simulationDays - checkInDay;

                final int daysToBeAddedToCheckOut = random.nextInt(daysLeftAfterCheckIn) + 1;

                int checkOutDay = 0;
                if (checkInDay + daysToBeAddedToCheckOut > simulationDays) {
                    checkOutDay = simulationDays;
                } else {
                    checkOutDay = checkInDay + daysToBeAddedToCheckOut;
                }
                Preconditions.checkArgument(checkOutDay <= simulationDays);
                Preconditions.checkArgument(checkOutDay - checkInDay >= 1, "CheckoutDay {} checkInDay {}", checkOutDay, checkInDay);

                final int totalBudget = randomBudget * (checkOutDay - checkInDay);

                return new Client(totalBudget, checkInDay, checkOutDay);
            }

        }
    }
}
