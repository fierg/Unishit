package de.unitrier.st.PII.oueb03.group2.aufgabe2;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static java.lang.System.out;

public class PiTrialFrame extends JFrame implements Runnable
{
    private JLabel estimatePiLabel;
    private JLabel trialCountLabel;
    private JButton startButton;
    private JSpinner threadsSpinner;
    boolean running;
    private boolean onceStarted;
    private boolean update;


    // TODO: Code hier hinzufuegen
    private JLabel timeElapsedLabel;
    ElapsedTimeUpdateThread timeUpdateThread;

    private static final int DEFAULT_THREADS = Math.max(1, Runtime.getRuntime().availableProcessors() - 1);

    PiTrialFrame()
    {
        super("Pi-Trial");

        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException |
                IllegalAccessException | UnsupportedLookAndFeelException e)
        {
            e.printStackTrace();
        }

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(300, 180));

        Container cp = getContentPane();

        setLayout(new BorderLayout());
        JPanel jPanel = new JPanel();

        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.PAGE_AXIS));
        Border border = BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1);

        Integer[] possibleNrOfThreads = new Integer[]{1, 2, 3, 4, 5, 6, 7};
        SpinnerListModel spinnerModel = new SpinnerListModel(possibleNrOfThreads);
        threadsSpinner = new JSpinner(spinnerModel);
        threadsSpinner.setValue(DEFAULT_THREADS);
        JLabel jLabel3 = new JLabel("  Number of threads to use: ");
        JPanel jPanel5 = new JPanel();
        jPanel5.setLayout(new BorderLayout());
        jPanel5.add(jLabel3, BorderLayout.WEST);
        jPanel5.add(threadsSpinner, BorderLayout.CENTER);
        jPanel.add(jPanel5);

        JLabel jLabel = new JLabel("  Actual value of pi: ");
        JPanel jPanel1 = new JPanel();
        jPanel1.setLayout(new BorderLayout());
        jPanel1.add(jLabel, BorderLayout.WEST);
        JLabel jLabel1 = new JLabel(String.format("%.15f  ", Math.PI));
        jPanel1.setBorder(border);
        jPanel1.add(jLabel1, BorderLayout.EAST);
        jPanel.add(jPanel1);

        JLabel jLabel2 = new JLabel("  Approximated value of pi: ");
        JPanel jPanel2 = new JPanel();
        jPanel2.setLayout(new BorderLayout());
        jPanel2.add(jLabel2, BorderLayout.WEST);
        estimatePiLabel = new JLabel(String.format("%.15f  ", 0f));
        jPanel2.add(estimatePiLabel, BorderLayout.EAST);
        jPanel2.setBorder(border);
        jPanel.add(jPanel2);

        JLabel jLabel4 = new JLabel("  Number of trials: ");
        JPanel jPanel3 = new JPanel();
        jPanel3.setLayout(new BorderLayout());
        jPanel3.add(jLabel4, BorderLayout.WEST);
        trialCountLabel = new JLabel(String.format("%d  ", 0));
        jPanel3.add(trialCountLabel, BorderLayout.EAST);
        jPanel3.setBorder(border);
        jPanel.add(jPanel3);

        // TODO: Code hier hinzufuegen
        JLabel jLabel5 = new JLabel("  Time elapsed:  ");
        JPanel jPanel4 = new JPanel();
        jPanel4.setLayout(new BorderLayout());
        jPanel4.add(jLabel5, BorderLayout.WEST);
        timeElapsedLabel = new JLabel(String.format("%.2f  ",0f));
        jPanel.add(jPanel4);

        running = false;
        onceStarted = false;

        startButton = new JButton();

        final PiTrialFrame frame = this;

        startButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                synchronized (frame)
                {
                    if (done())
                    {
                        threadsSpinner.setEnabled(false);
                        running = true;

                        // TODO: Code hier hinzufuegen


                        init();
                        initThreads();
                        frame.notifyAll();
                        start = System.currentTimeMillis();
                        return;
                    }
                    if (running)
                    {
                        timeElapsed += System.currentTimeMillis() - start;
                        running = false;
                    } else
                    {
                        if (!onceStarted)
                        {
                            onceStarted = true;
                            threadsSpinner.setEnabled(false);
                        }
                        running = true;
                        frame.notifyAll();
                        start = System.currentTimeMillis();
                    }
                }
            }
        });

        cp.add(startButton, BorderLayout.SOUTH);
        cp.add(jPanel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        init();
    }

    private int parseSpinnerInput()
    {
        int value;
        try
        {
            value = (Integer) threadsSpinner.getValue();
        } catch (Exception e)
        {
            return DEFAULT_THREADS;
        }
        return value;
    }

    private void init()
    {
        trials = 0;
        inCircle = 0;
        doneThreads = 0;
        timeElapsed = 0;
        startButton.setText("Start / Pause");
        // TODO: Code hier hinzufuegen
        timeUpdateThread = new ElapsedTimeUpdateThread(this);
        timeUpdateThread.start();
    }

    private void initThreads()
    {
        nrOfThreads = parseSpinnerInput();
        long start = System.currentTimeMillis();
        for (int i = 0; i < nrOfThreads; i++)
        {
            new PiTrialThread(this, Integer.MAX_VALUE / 4 / nrOfThreads).start();
        }
        timeElapsed += System.currentTimeMillis() - start;
        out.printf("Time to create the threads = %d ms\n", timeElapsed);
    }

    private int nrOfThreads;
    private int doneThreads;
    long start;
    long timeElapsed;
    private long trials;
    private long inCircle;

    private final Object updateLock = new Object();

    void addTrials(long trials, long inCircle)
    {
        synchronized (updateLock)
        {
            this.trials += trials;
            this.inCircle += inCircle;
            this.update = true;
            updateLock.notify();
        }
    }
    public void setTimeElapsedLabel(long timeElapsed){
        this.timeElapsedLabel.setText(String.valueOf(timeElapsed));
    }

    private final Object doneLock = new Object();

    void threadDone()
    {
        synchronized (doneLock)
        {
            out.printf("%s done\n", Thread.currentThread());
            if (++doneThreads == nrOfThreads)
            {
                startButton.setText("Restart");
                onceStarted = false;
                threadsSpinner.setEnabled(true);

                // TODO: Code hier hinzufuegen
                timeUpdateThread = new ElapsedTimeUpdateThread(this);


            }
        }
    }


    private boolean done()
    {
        synchronized (doneLock)
        {
            return doneThreads == nrOfThreads;
        }
    }

    @Override
    public void run()
    {
        while (!Thread.currentThread().isInterrupted())
        {
            synchronized (updateLock)
            {
                try
                {
                    while (!update)
                    {
                        updateLock.wait();
                    }
                    estimatePiLabel.setText(String.format("%.15f  ", 4 * ((double) inCircle / trials)));
                    trialCountLabel.setText(String.format("%d  ", trials));
                    update = false;
                } catch (InterruptedException e)
                {
                    break;
                }
            }
        }
    }
}
