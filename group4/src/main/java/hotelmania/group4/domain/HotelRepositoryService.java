package hotelmania.group4.domain;


import hotelmania.ontology.Hotel;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 20/04/14
 */
public interface HotelRepositoryService {

    void registerHotel (Hotel hotel) throws HotelAlreadyRegisteredException;
}
