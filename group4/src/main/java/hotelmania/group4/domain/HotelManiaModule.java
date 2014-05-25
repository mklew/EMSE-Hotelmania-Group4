package hotelmania.group4.domain;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import hotelmania.group4.domain.internal.*;
import hotelmania.group4.settings.Settings;
import hotelmania.group4.settings.SettingsLoader;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 21/04/14
 */
public class HotelManiaModule extends AbstractModule {

    @Override
    protected void configure () {
        bind(HotelRepositoryService.class).to(InMemoryHotelRepositoryService.class).in(Scopes.SINGLETON);

        bind(BankAccountRepository.class).to(InMemoryBankAccountRepository.class).in(Scopes.SINGLETON);

        bind(HotelManiaCalendar.class).to(HotelManiaCalendarImpl.class).in(Scopes.SINGLETON);

        bind(Hotel4.class).to(Hotel4Impl.class).in(Scopes.SINGLETON);

        bind(PriceStrategy.class).to(DummyPriceStrategy.class).in(Scopes.SINGLETON);

        SettingsLoader settingsLoader = new SettingsLoader();
        settingsLoader.init();

        bind(Settings.class).toInstance(settingsLoader);
    }
}
