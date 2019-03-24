package api_calls;

import utils.PCS;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import smartmirror.MirrorViewController;

public abstract class APIManager extends Thread implements PropertyChangeListener {

    private final long sleepTime;
    private final AtomicLong lastPull = new AtomicLong(0);
    private final AtomicBoolean stop = new AtomicBoolean(true);
    private final String STOP_PROP;
    private final String PULL_PROP;
    private final String errMsg;

    APIManager(long sleepTime, String pullProp, String stopProp, String errMsg) {
        this.sleepTime = sleepTime;
        this.STOP_PROP = stopProp;
        this.PULL_PROP = pullProp;
        this.errMsg = errMsg;
        PCS.INST.addPropertyChangeListener(STOP_PROP, this);
        PCS.INST.addPropertyChangeListener(PULL_PROP, this);
    }

    private boolean doPull() {
        return (System.currentTimeMillis() - lastPull.get() > sleepTime);
    }
    
    private void pullApi() {
        try {
            fetch();
            lastPull.set(System.currentTimeMillis());
        } catch (IOException ex) {
            System.out.println(ex);
            MirrorViewController.putAlert(errMsg);
        }
    }
    
    abstract protected void fetch() throws IOException;

    @Override
    public void run() {
        stop.set(false);
        while(!stop.get()){        
            if (doPull()) pullApi();
            try {
                Thread.sleep(sleepTime);
            } 
            catch (InterruptedException ex) {
                break;
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(STOP_PROP)){
            stop.set(true);
            this.interrupt();
        }
        else if (evt.getPropertyName().equals(PULL_PROP)) {
            if (!stop.get()){
                pullApi();
            }
        }
    }
}
