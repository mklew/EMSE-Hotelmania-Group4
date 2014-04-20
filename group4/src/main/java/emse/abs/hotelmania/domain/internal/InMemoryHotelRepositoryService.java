package emse.abs.hotelmania.domain.internal;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import emse.abs.hotelmania.domain.HotelAlreadyRegisteredException;
import emse.abs.hotelmania.domain.HotelRepositoryService;
import emse.abs.hotelmania.ontology.Hotel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 21/04/14
 */
public class InMemoryHotelRepositoryService implements HotelRepositoryService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private Set<Hotel> registeredHotels = new HashSet<Hotel>();

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
}
