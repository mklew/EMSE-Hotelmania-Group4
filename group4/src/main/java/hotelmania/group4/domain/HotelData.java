package hotelmania.group4.domain;

import com.google.common.base.Optional;
import hotelmania.ontology.Contract;
import hotelmania.ontology.Hotel;
import hotelmania.ontology.HotelInformation;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 26/05/14
 */
public class HotelData {
    private final HotelInformation hotelInformation;

    private Map<Integer, Contract> dayToSignedContract = new HashMap<>();

    public HotelInformation getHotelInformation () {
        return hotelInformation;
    }

    public HotelData (HotelInformation hotelInformation) {
        this.hotelInformation = hotelInformation;
    }

    Optional<Contract> getContractForDay (int dayNumber) {
        return Optional.fromNullable(dayToSignedContract.get(dayNumber));
    }

    public Hotel getHotel () {
        return hotelInformation.getHotel();
    }

    public void saveContract (Contract contract) {
        if (getContractForDay(contract.getDay()).isPresent()) {
            throw new RuntimeException("There should be no contract");
        }
        dayToSignedContract.put(contract.getDay(), contract);
    }
}
