package emse.abs.hotelmania.behaviours;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 20/04/14
 */
public abstract class EmseCyclicBehaviour extends EmseSimpleBehaviour {

    @Override public boolean done () {
        return false;
    }
}
