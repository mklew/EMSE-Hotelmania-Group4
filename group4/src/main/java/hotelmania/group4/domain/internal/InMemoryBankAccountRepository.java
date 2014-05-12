package hotelmania.group4.domain.internal;

import hotelmania.group4.domain.AccountAlreadyExistsException;
import hotelmania.group4.domain.BankAccountRepository;
import hotelmania.ontology.Account;
import hotelmania.ontology.Hotel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

/**
 * Created by Tahir on 09/05/2014.
 */
public class InMemoryBankAccountRepository implements BankAccountRepository {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private Hashtable accounts = new Hashtable();
    @Override
    public synchronized int createAccount(final Hotel hotel) throws AccountAlreadyExistsException {
        if (accounts.containsKey(hotel.getHotel_name()))
        {
            logger.debug("Account with title {} has already been Created", hotel.getHotel_name());
            throw new AccountAlreadyExistsException();
        }
        else
        {
            if (accounts.isEmpty())
            {
                int balance = 100;
                Account newAccount = new Account();
                newAccount.setBalance(balance);
                newAccount.setHotel(hotel);
                newAccount.setId_account(1001);
                accounts.put(1001, newAccount);
                logger.info("Created Account with ID 1001 in Bank4");
                return 1001;
            }
            else
            {
                int balance = 100;
                int key = 1000 + accounts.size();
                Account newAccount = new Account();
                newAccount.setBalance(balance);
                newAccount.setHotel(hotel);
                newAccount.setId_account(key);

                accounts.put(key, newAccount);
                logger.info("Created Account with ID {} in Bank4", key);
                return key;
            }
        }
    }

    public synchronized Account retrieveBalance (int account_ID) throws AccountDoesNotExistException
    {
        Account account = (Account)accounts.get(account_ID);

        return account;
    }
}
