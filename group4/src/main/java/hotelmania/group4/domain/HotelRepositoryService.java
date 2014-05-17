package hotelmania.group4.domain;


import hotelmania.ontology.Hotel;

import java.util.Set;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 20/04/14
 */
public interface HotelRepositoryService {

    void registerHotel (Hotel hotel) throws HotelAlreadyRegisteredException;

    Set<Hotel> getHotels ();
}
