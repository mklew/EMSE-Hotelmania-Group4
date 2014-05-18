package hotelmania.group4.domain;

import hotelmania.ontology.Price;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 18/05/14
 */
public class CurrentPriceIsHigherException extends Exception {

    private final Price agreedPrice;

    private final Price currentPrice;

    public CurrentPriceIsHigherException (Price currentPrice, Price agreedPrice) {
        this.currentPrice = currentPrice;
        this.agreedPrice = agreedPrice;
    }

    public Price getAgreedPrice () {
        return agreedPrice;
    }

    public Price getCurrentPrice () {
        return currentPrice;
    }
}
