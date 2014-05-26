package hotelmania.group4.domain;


import hotelmania.group4.domain.internal.HotelHasNotBeenRegistered;
import hotelmania.ontology.Hotel;
import hotelmania.ontology.HotelInformation;

import java.util.Set;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 20/04/14
 */
public interface HotelRepositoryService {

    void registerHotel (Hotel hotel) throws HotelAlreadyRegisteredException;

    Set<Hotel> getHotels ();

    Set<HotelInformation> getHotelInformation ();

    HotelData getHotel (Hotel hotel) throws HotelHasNotBeenRegistered;
}
