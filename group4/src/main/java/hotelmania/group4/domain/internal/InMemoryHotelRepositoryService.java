package hotelmania.group4.domain.internal;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Sets;
import hotelmania.group4.domain.HotelAlreadyRegisteredException;
import hotelmania.group4.domain.HotelData;
import hotelmania.group4.domain.HotelRepositoryService;
import hotelmania.ontology.Hotel;
import hotelmania.ontology.HotelInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
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

    private Set<HotelData> registeredHotels = new HashSet<>();


    @Override
    public synchronized void registerHotel (final Hotel hotel) throws HotelAlreadyRegisteredException {

        final Optional<HotelData> hotelOpt = getHotelInformationOptional(hotel);

        if (hotelOpt.isPresent()) {
            logger.debug("Hotel with name {} has already been registered", hotel.getHotel_name());
            throw new HotelAlreadyRegisteredException();
        } else {
            HotelInformation hotelInformation = new HotelInformation();
            hotelInformation.setRating(5);
            hotelInformation.setHotel(hotel);
            final HotelData hotelData = new HotelData(hotelInformation);
            registeredHotels.add(hotelData);
            logger.info("Registered hotel with name {} in hotel mania", hotel.getHotel_name());
        }
    }

    private Optional<HotelData> getHotelInformationOptional (final Hotel hotel) {
        return (Optional<HotelData>) FluentIterable.from(registeredHotels).filter(new Predicate<HotelData>() {
            @Override public boolean apply (HotelData registeredHotel) {
                return registeredHotel.getHotelInformation().getHotel().getHotel_name().equals(hotel.getHotel_name());
            }
        }).first();
    }

    @Override
    public Set<Hotel> getHotels () {
        final Collection<Hotel> transform = Collections2.transform(registeredHotels, new Function<HotelData, Hotel>() {
            @Override
            public Hotel apply (HotelData hotelData) {
                return hotelData.getHotelInformation().getHotel();
            }
        });
        return Sets.newHashSet(transform);
    }

    @Override public Set<HotelInformation> getHotelInformation () {
        return FluentIterable.from(registeredHotels).transform(new Function<HotelData, HotelInformation>() {
            @Override public HotelInformation apply (HotelData input) {
                return input.getHotelInformation();
            }
        }).toSet();
    }

    @Override public HotelData getHotel (Hotel hotel) throws HotelHasNotBeenRegistered {
        final Optional<HotelData> hotelDataOpt = getHotelInformationOptional(hotel);
        if (hotelDataOpt.isPresent()) {
            return hotelDataOpt.get();
        } else {
            throw new HotelHasNotBeenRegistered();
        }
    }
}
