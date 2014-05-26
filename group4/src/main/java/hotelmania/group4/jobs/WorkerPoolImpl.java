package hotelmania.group4.jobs;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 26/05/14
 */
public class WorkerPoolImpl implements WorkerPool {

    ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Override public void submitJob (final Job job) {

        executorService.submit(new Callable<Object>() {
            @Override public Object call () throws Exception {
                job.doJob();
                return null;
            }
        });
    }
}
