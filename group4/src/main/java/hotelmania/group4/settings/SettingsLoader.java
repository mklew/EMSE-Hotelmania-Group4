package hotelmania.group4.settings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 25/05/14
 */
public class SettingsLoader implements Settings {

    private Properties properties;

    Logger logger = LoggerFactory.getLogger(getClass());

    @Override public double getClientBudget () {
        return Double.parseDouble(properties.getProperty("clients.budget"));
    }

    @Override public double getBudgetVariance () {
        return Double.parseDouble(properties.getProperty("clients.budget_variance"));
    }

    @Override public int getSimulationDays () {
        return Integer.parseInt(properties.getProperty("simulation.days"));
    }

    @Override public int getNumberOfNewClientsPerDay () {
        return Integer.parseInt(properties.getProperty("simulation.clients_per_day"));
    }

    @Override public int getTimeToStart () {
        return Integer.parseInt(properties.getProperty("simulation.time_to_start"));
    }

    @Override public int getDayLengthInSeconds () {
        return Integer.parseInt(properties.getProperty("day.length"));
    }


    public void init () {
        this.properties = loadProperties();
        logger.info("Loaded properties: " + properties.toString());
    }

    Properties loadProperties () {
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream("default.properties")) {
            final Properties props = new Properties();
            props.load(stream);
            final String propertiesName = getPropertiesName();
            try (InputStream propsStream = getClass().getClassLoader().getResourceAsStream(propertiesName)) {
                if (propsStream != null) {
                    props.load(propsStream);
                }
                return props;
            } catch (Exception e) {
                throw new RuntimeException("Failed to load properties", e);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load default properties", e);
        }
    }

    private String getPropertiesName () {
        return System.getProperty("settings.name", "settings.properties");
    }
}
