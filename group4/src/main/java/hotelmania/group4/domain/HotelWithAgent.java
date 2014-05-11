package hotelmania.group4.domain;

import hotelmania.ontology.Hotel;
import jade.core.AID;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 12/05/14
 */
public class HotelWithAgent {

    private final Hotel hotel;

    private final AID agent;

    public HotelWithAgent (Hotel hotel, AID agent) {
        this.hotel = hotel;
        this.agent = agent;
    }


    public String getHotelName () {
        return hotel.getHotel_name();
    }

    public AID getAgent () {
        return agent;
    }
}
