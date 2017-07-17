package de.unitrier.st.PII.oueb03.group2.aufgabe2;

import java.util.Random;

public final class PiTrialThread extends Thread
{
    private final PiTrialFrame trialFrame;

    private final long maxTrials;

    private static int threadInitNumber;

    private static synchronized int nextThreadNum()
    {
        return threadInitNumber++;
    }

    PiTrialThread(PiTrialFrame trialFrame, long maxTrials)
    {
        super("PiTrialThread-" + nextThreadNum());
        this.trialFrame = trialFrame;
        this.maxTrials = maxTrials;
    }

    @Override
    public void run()
    {
        Random random = new Random();
        long end = maxTrials;
        final long BATCH_SIZE = 1000000;
        long loopEnd = BATCH_SIZE;

        while (!isInterrupted() && end > 0)
        {
            try
            {
                synchronized (trialFrame)
                {
                    while (!trialFrame.running)
                    {
                        trialFrame.wait();
                    }
                }

                long inCircleCount = 0;
                long trialCount = 0;

                if (end > BATCH_SIZE)
                {
                    end = end - BATCH_SIZE;
                } else
                {
                    loopEnd = end;
                    end = 0;
                }

                for (long i = 0; i < loopEnd; i++)
                {
                    double x = random.nextDouble();
                    double y = random.nextDouble();
                    if (x * x + y * y < 1)
                    {
                        inCircleCount++;
                    }
                    trialCount++;
                }

                trialFrame.addTrials(trialCount, inCircleCount);
            } catch (InterruptedException e)
            {
                break;
            }
        }
        trialFrame.threadDone();
    }
}
