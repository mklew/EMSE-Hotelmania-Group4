package emse.abs.hotelmania.group4;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import emse.abs.hotelmania.ontology.Hotel;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 20/04/14
 */
public class AgPlatform4 extends HotelManiaAgent implements HotelMania {

    Logger logger = LoggerFactory.getLogger(getClass());

    private Set<Hotel> registeredHotels = new HashSet<Hotel>();

    @Override
    protected void setupHotelManiaAgent () {
        logger.debug("setting up agent");
        try {
            // Creates its own description
            DFAgentDescription dfd = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setName(this.getName());
            sd.setType(HOTELMANIA);
            dfd.addServices(sd);
            // Registers its description in the DF
            DFService.register(this, dfd);
            logger.info(getLocalName() + ": registered in the DF");
        } catch (FIPAException e) {
            e.printStackTrace();
        }

        addBehaviour(new RegistrationBehaviour(this));
    }


    @Override
    public void registerHotel (final Hotel hotel) throws HotelAlreadyRegisteredException {
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
