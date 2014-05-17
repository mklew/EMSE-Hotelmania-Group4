package hotelmania.group4.domain.internal;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import hotelmania.group4.domain.HotelAlreadyRegisteredException;
import hotelmania.group4.domain.HotelRepositoryService;
import hotelmania.ontology.Hotel;
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

    private Set<Hotel> registeredHotels = new HashSet<>();

    @Override
    public synchronized void registerHotel (final Hotel hotel) throws HotelAlreadyRegisteredException {
        final Collection<Hotel> filtered = Collections2.filter(registeredHotels, new Predicate<Hotel>() {
            @Override public boolean apply (Hotel registeredHotel) {
                return registeredHotel.getHotel_name().equals(hotel.getHotel_name());
            }
        });
        if (filtered.size() > 0) {
            logger.debug("Hotel with name {} has already been registered", hotel.getHotel_name());
            throw new HotelAlreadyRegisteredException();
        } else {
            registeredHotels.add(hotel);
            logger.info("Registered hotel with name {} in hotel mania", hotel.getHotel_name());
        }
    }

    @Override
    public Set<Hotel> getHotels () {
        return Collections.unmodifiableSet(registeredHotels);
    }
}
