package emse.abs.hotelmania.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import emse.abs.hotelmania.domain.HotelManiaModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 21/04/14
 */
public class GuiceConfigurer {

    private static AtomicReference<Injector> injector;

    private static Logger logger = LoggerFactory.getLogger(GuiceConfigurer.class);

    static {
        logger.debug("Getting container up");
        injector = new AtomicReference<Injector>(Guice.createInjector(new HotelManiaModule()));
    }

    public static Injector getInjector () {
        return injector.get();
    }
}
