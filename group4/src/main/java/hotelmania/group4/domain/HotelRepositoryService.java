package hotelmania.group4.domain;


import java.util.Set;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 20/04/14
 */
public interface HotelRepositoryService {

    void registerHotel (HotelWithAgent hotel) throws HotelAlreadyRegisteredException;

    Set<HotelWithAgent> getHotels();
}
