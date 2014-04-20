package emse.abs.hotelmania.group4;

import emse.abs.hotelmania.ontology.Hotel;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 20/04/14
 */
public interface HotelMania {

    void registerHotel (Hotel hotel) throws HotelAlreadyRegisteredException;
}
