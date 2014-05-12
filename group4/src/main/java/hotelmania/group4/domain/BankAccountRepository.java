package hotelmania.group4.domain;

import hotelmania.group4.domain.internal.AccountDoesNotExistException;
import hotelmania.ontology.Hotel;

/**
 * Created by Tahir on 09/05/2014.
 */
public interface BankAccountRepository {
    int createAccount (Hotel hotel) throws AccountAlreadyExistsException;
    int retrieveBalance (int account_ID) throws AccountDoesNotExistException;
}
