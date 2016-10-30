package unm.cs351.FibinacciWalk;

import java.util.ArrayList;

/**
 * A simple thread and sync example that generates two threads that run
 * independent Fibinacci sequences and terminate after forty seconds.
 * 
 * 
 * Created by Nicholas Spurlock
 */
public class FibinacciWalk
{
  private ArrayList<FibinacciThread> threads = new ArrayList<>();
  volatile boolean stop = false;

  public FibinacciWalk()
  {
    threads.add(new FibinacciThread("Thread1"));
    threads.add(new FibinacciThread("Thread2"));
  }

  private class FibinacciThread extends Thread
  {

    private long[] values = new long[3];
    private final String NAME;
    private long step = 0;
    private long z;
    private long y = 1;
    private long x = 1;

    public FibinacciThread(final String name)
    {
      this.NAME = name;
    }

    @Override
    public void run()
    {
      while (!stop)
      {
        if (z == 7540113804746346429L)
        {
          x = y = 1;
        }
        modifyFib(false);
        step++;
      }
      System.out.println("Thread " + NAME + " terminating.");
      return;
    }

    public String returnName()
    {
      return NAME;
    }

    public long getStep()
    {
      return step;
    }

    /**
     * Synchronized meathod used to update or return current fibinacci values
     * 
     * @param getValues
     *          true if we just want to return current x,y,z values
     * @return current values if getValues is true otherwise null
     */
    public synchronized long[] modifyFib(boolean getValues)
    {
      if (getValues)
      {
        long[] v = { values[0], values[1], values[2] };
        return v;
      }
      z = x + y;

      // Save values for when we ask for them
      values[0] = x;
      values[1] = y;
      values[2] = z;

      x = y;
      y = z;
      return null;
    }
  }

  public static void main(String[] argv)
  {
    FibinacciWalk fw = new FibinacciWalk();
    for (FibinacciThread t : fw.threads)
    {
      t.start();
    }
    for (int i = 0; i < 20; i++)
    {
      try
      {
        if (fw.threads.get(0).isAlive() && fw.threads.get(1).isAlive())
        {
          Thread.sleep(2000);
          for (FibinacciThread t : fw.threads)
          {
            long[] values = t.modifyFib(true);
            assert (values[0] + values[1] == values[2]);
            System.out.println("Thread Name: " + t.returnName());
            System.out.println("Thread Step: " + t.getStep());
            System.out.println("Thread (x,y,z): (" + values[0] + ", " + values[1] + ", " + values[2] + ")");
          }
          System.out.println();
        } else
        {
          System.err.println("One of the threads has stopped before its time!");
          break;
        }
      } catch (InterruptedException e)
      {
        e.printStackTrace();
      }
    }
    // Out of the loop, stop the threads
    fw.stop = true;

    // Wait for threads to finish and exit
    while (fw.threads.get(0).isAlive() || fw.threads.get(1).isAlive())
    {
      try
      {
        Thread.sleep(500);
      } catch (InterruptedException e)
      {
        // Don't really care if we get interrupted we're exiting anyway
      }
    }
    // Both threads are dead, exit
    System.out.println("Main thread exiting, goodbye!");
    System.exit(0);

  }

}
