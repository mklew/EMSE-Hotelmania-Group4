package hotelmania.group4.domain.internal;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import hotelmania.group4.domain.HotelAlreadyRegisteredException;
import hotelmania.group4.domain.HotelRepositoryService;
import hotelmania.group4.domain.HotelWithAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Should be thread-safe
 *
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 21/04/14
 */
public class InMemoryHotelRepositoryService implements HotelRepositoryService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private Set<HotelWithAgent> registeredHotels = new HashSet<>();

    @Override
    public synchronized void registerHotel (final HotelWithAgent hotel) throws HotelAlreadyRegisteredException {
        final Collection<HotelWithAgent> filtered = Collections2.filter(registeredHotels, new Predicate<HotelWithAgent>() {
            @Override public boolean apply (HotelWithAgent registeredHotel) {
                return registeredHotel.getHotelName().equals(hotel.getHotelName());
            }
        });
        if (filtered.size() > 0) {
            logger.debug("Hotel with name {} has already been registered", hotel.getHotelName());
            throw new HotelAlreadyRegisteredException();
        } else {
            registeredHotels.add(hotel);
            logger.info("Registered hotel with name {} in hotel mania", hotel.getHotelName());
        }
    }

    @Override
    public Set<HotelWithAgent> getHotels () {
        return Collections.unmodifiableSet(registeredHotels);
    }
}
