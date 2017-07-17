package de.unitrier.st.PII.oueb03.group2.aufgabe2;

public class ElapsedTimeUpdateThread extends Thread
{
    // TODO: Code hier hinzufuegen
    private long timeElapsed = 0;
    private boolean running;
    private PiTrialFrame trialFrame;

    ElapsedTimeUpdateThread(PiTrialFrame frame)
    {
        this.running = false;
        this.timeElapsed = 0;
        this.trialFrame = frame;
    }

    @Override
    public void run()
    {
        // TODO: Code hier hinzufuegen
        while (running){
            long time = System.currentTimeMillis();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            timeElapsed = System.currentTimeMillis() - time;
            trialFrame.setTimeElapsedLabel(timeElapsed);

        }
        
    }
}
