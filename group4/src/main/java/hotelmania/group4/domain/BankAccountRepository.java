package hotelmania.group4.domain;

import hotelmania.group4.domain.internal.AccountDoesNotExistException;
import hotelmania.ontology.Account;
import hotelmania.ontology.Hotel;

/**
 * Created by Tahir on 09/05/2014.
 */
public interface BankAccountRepository {
    Account createAccount (Hotel hotel) throws AccountAlreadyExistsException;

    Account retrieveBalance (int account_ID) throws AccountDoesNotExistException;

    void chargeHotel (Hotel hotel, float contractValue);
}
