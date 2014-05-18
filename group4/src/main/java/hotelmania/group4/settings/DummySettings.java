package hotelmania.group4.settings;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 18/05/14
 */
public class DummySettings implements Settings {

    @Override public double getClientBudget () {
        return 400;
    }

    @Override public double getBudgetVariance () {
        return 100;
    }

    @Override public int getSimulationDays () {
        return 50;
    }

    @Override public int getNumberOfNewClientsPerDay () {
        return 10;
    }
}
