package hotelmania.group4.behaviours;

import hotelmania.group4.HotelManiaAgent;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 20/04/14
 */
public abstract class EmseCyclicBehaviour extends EmseSimpleBehaviour {

    public EmseCyclicBehaviour (HotelManiaAgent agent) {
        super(agent);
    }

    @Override public boolean done () {
        return false;
    }
}
