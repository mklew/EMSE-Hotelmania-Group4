package hotelmania.group4.domain;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import hotelmania.group4.domain.internal.InMemoryBankAccountRepository;
import hotelmania.group4.domain.internal.InMemoryHotelRepositoryService;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 21/04/14
 */
public class HotelManiaModule extends AbstractModule {

    @Override
    protected void configure () {
        bind(HotelRepositoryService.class).to(InMemoryHotelRepositoryService.class).in(Scopes.SINGLETON);
        bind(BankAccountRepository.class).to(InMemoryBankAccountRepository.class).in(Scopes.SINGLETON);
    }
}
