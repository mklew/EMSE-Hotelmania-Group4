package hotelmania.group4.domain;

import hotelmania.ontology.Stay;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 17/05/14
 */
public class NoRoomsAvailableException extends Exception {
    private final Stay stay;

    public NoRoomsAvailableException (Stay stay) {
        this.stay = stay;
    }
}
