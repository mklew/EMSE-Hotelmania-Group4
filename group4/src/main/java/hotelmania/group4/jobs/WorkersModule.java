package hotelmania.group4.jobs;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 26/05/14
 */
public class WorkersModule extends AbstractModule {
    @Override protected void configure () {
        bind(WorkerPool.class).to(WorkerPoolImpl.class).in(Scopes.SINGLETON);
    }
}
