package hotelmania.group4.domain;

import hotelmania.ontology.Hotel;

/**
 * Created by Tahir on 09/05/2014.
 */
public interface BankAccountRepository {
    void createAccount (Hotel hotel) throws AccountAlreadyExistsException;
}
