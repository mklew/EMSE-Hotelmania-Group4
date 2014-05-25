package hotelmania.group4.domain.client;

import hotelmania.group4.settings.Settings;
import org.testng.annotations.Test;

import java.util.Random;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 18/05/14
 */
public class ClientGeneratorTest {

    public static final int SIMULATION_DAYS = 50;

    @Test(invocationCount = 1000)
    public void testGenerateClient () throws Exception {
        final ClientGenerator clientGenerator = new ClientGenerator(new Settings() {
            @Override public double getClientBudget () {
                return 100;
            }

            @Override public double getBudgetVariance () {
                return 20;
            }

            @Override public int getSimulationDays () {
                return SIMULATION_DAYS;
            }

            @Override public int getNumberOfNewClientsPerDay () {
                return 10;
            }

            @Override public int getTimeToStart () {
                return 0;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override public int getDayLengthInSeconds () {
                return 0;  //To change body of implemented methods use File | Settings | File Templates.
            }
        });
        final Random random = new Random();
        final int currentDay = random.nextInt(SIMULATION_DAYS);

        final Client client = clientGenerator.generateClient(currentDay);

        assertThat(client.getCheckInDay() >= currentDay).isTrue();
        assertThat(client.getCheckOutDay() <= SIMULATION_DAYS + 1).isTrue();
    }
}
