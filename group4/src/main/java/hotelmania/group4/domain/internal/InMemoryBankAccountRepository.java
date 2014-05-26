package hotelmania.group4.domain.internal;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import hotelmania.group4.domain.AccountAlreadyExistsException;
import hotelmania.group4.domain.BankAccountRepository;
import hotelmania.ontology.Account;
import hotelmania.ontology.Hotel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tahir on 09/05/2014.
 */
public class InMemoryBankAccountRepository implements BankAccountRepository {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private Map<Integer, Account> accounts = new HashMap<>();

    private int accountId = 100;

    @Override
    public synchronized Account createAccount (final Hotel hotel) throws AccountAlreadyExistsException {

        int newId = getNextAccountId();

        if (containsAccount(hotel)) {
            logger.debug("Account for hotel with name '{}' already exists", hotel.getHotel_name());
            throw new AccountAlreadyExistsException();
        } else {
            Account newAccount = new Account();
            newAccount.setBalance(0);
            newAccount.setHotel(hotel);
            newAccount.setId_account(newId);
            accounts.put(newId, newAccount);
            logger.info("Created Account with ID {} in Bank4 for hotel '{}'", newId, hotel.getHotel_name());
            return newAccount;
        }
    }

    private boolean containsAccount (final Hotel hotel) {
        return findAccounts(hotel).size() > 0;
    }

    private Collection<Account> findAccounts (final Hotel hotel) {
        return Collections2.filter(accounts.values(), new Predicate<Account>() {
            @Override public boolean apply (Account account) {
                return account.getHotel().getHotel_name().equals(hotel.getHotel_name());
            }
        });
    }

    private int getNextAccountId () {
        final int id = accountId;
        accountId = accountId + 1;
        return id;
    }

    public synchronized Account retrieveAccount (int accountId) throws AccountDoesNotExistException {
        Account account = accounts.get(accountId);
        if(account == null) {
            throw new AccountDoesNotExistException();
        }
        return account;
    }

    @Override public void chargeHotel (Hotel hotel, float contractValue) {
        try {
            Account acc = getAccount(hotel);
            final float balance = acc.getBalance();
            final float newBalance = balance - contractValue;
            acc.setBalance(newBalance);
        } catch (AccountDoesNotExistException e) {
            throw new RuntimeException(e);
        }
    }

    private Account getAccount (Hotel hotel) throws AccountDoesNotExistException {
        final Collection<Account> accounts = findAccounts(hotel);
        if (accounts.size() == 0) {
            throw new AccountDoesNotExistException();
        } else {
            return accounts.iterator().next();
        }
    }
}
