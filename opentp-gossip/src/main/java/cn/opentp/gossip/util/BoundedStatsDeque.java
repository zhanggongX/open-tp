package cn.opentp.gossip.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * bounded threadsafe deque
 */
public class BoundedStatsDeque extends AbstractStatsDeque
{
    protected final LinkedBlockingDeque<Double> deque;

    public BoundedStatsDeque(int size)
    {
        deque = new LinkedBlockingDeque<Double>(size);
    }

    public Iterator<Double> iterator()
    {
        return deque.iterator();
    }

    public int size()
    {
        return deque.size();
    }

    public void clear()
    {
        deque.clear();
    }

    public void add(double i)
    {
        if (!deque.offer(i))
        {
            try
            {
                deque.remove();
            }
            catch (NoSuchElementException e)
            {
                // oops, clear() beat us to it
            }
            deque.offer(i);
        }
    }
}
