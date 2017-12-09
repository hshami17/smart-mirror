package api_calls;

import utils.PCS;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public abstract class APIManager extends Thread implements PropertyChangeListener {

    private final long sleepTime;
    private boolean stop = true;
    private final String STOP_PROP;
    private final String PULL_PROP;

    APIManager(long sleepTime, String pullProp, String stopProp) {
        this.sleepTime = sleepTime;
        this.STOP_PROP = stopProp;
        this.PULL_PROP = pullProp;
        PCS.INST.addPropertyChangeListener(STOP_PROP, this);
        PCS.INST.addPropertyChangeListener(PULL_PROP, this);
    }

    abstract public void fetch();

    @Override
    public void run() {
        stop = false;
        while(!stop){
            try {
                fetch();
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
            stop = true;
            this.interrupt();
        }
        else if (evt.getPropertyName().equals(PULL_PROP)) {
            if (!stop){
                fetch();
            }
        }
    }
}
