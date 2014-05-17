package hotelmania.group4.domain.internal;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import hotelmania.group4.domain.HotelAlreadyRegisteredException;
import hotelmania.group4.domain.HotelRepositoryService;
import hotelmania.ontology.Hotel;
import hotelmania.ontology.HotelInformation;
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

    private Set<HotelInformation> registeredHotels = new HashSet<>();

    @Override
    public synchronized void registerHotel (final Hotel hotel) throws HotelAlreadyRegisteredException {
        final Collection<HotelInformation> filtered = Collections2.filter(registeredHotels, new Predicate<HotelInformation>() {
            @Override public boolean apply (HotelInformation registeredHotel) {
                return registeredHotel.getHotel().getHotel_name().equals(hotel.getHotel_name());
            }
        });
        if (filtered.size() > 0) {
            logger.debug("Hotel with name {} has already been registered", hotel.getHotel_name());
            throw new HotelAlreadyRegisteredException();
        } else {
            HotelInformation hotelInformation = new HotelInformation();
            hotelInformation.setRating(5); // TODO need to change that
            hotelInformation.setHotel(hotel);
            registeredHotels.add(hotelInformation);
            logger.info("Registered hotel with name {} in hotel mania", hotel.getHotel_name());
        }
    }

    @Override
    public Set<Hotel> getHotels () {
        final Collection<Hotel> transform = Collections2.transform(registeredHotels, new Function<HotelInformation, Hotel>() {
            @Override
            public Hotel apply (HotelInformation hotelInformation) {
                return hotelInformation.getHotel();
            }
        });
        return Sets.newHashSet(transform);
    }

    @Override public Set<HotelInformation> getHotelInformations () {
        return Collections.unmodifiableSet(registeredHotels);
    }
}
