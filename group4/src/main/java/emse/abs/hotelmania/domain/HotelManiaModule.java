package emse.abs.hotelmania.domain;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import emse.abs.hotelmania.domain.internal.InMemoryHotelRepositoryService;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 21/04/14
 */
public class HotelManiaModule extends AbstractModule {

    @Override
    protected void configure () {
        bind(HotelRepositoryService.class).to(InMemoryHotelRepositoryService.class).in(Scopes.SINGLETON);
    }
}
