package hotelmania.group4.jobs;

/**
 * @author Marek Lewandowski <marek.lewandowski@icompass.pl>
 * @since 26/05/14
 */
public interface WorkerPool {
    void submitJob(Job job);
}
