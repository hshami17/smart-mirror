package api_calls;

import utils.PCS;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;
import module.Module;
import smartmirror.MirrorViewController;

public abstract class APIManager implements PropertyChangeListener {
    
    protected Module module = null;
    
    private Thread apiThread = null;
    private final long pullInterval;
    private final AtomicLong lastPull = new AtomicLong(0);
    private final String PULL_PROP;

    APIManager(long pullInterval, String pullProp) {
        this.pullInterval = pullInterval;
        this.PULL_PROP = pullProp;
        PCS.INST.addPropertyChangeListener(PULL_PROP, this);
    }

    private boolean doPull() {
        return ((System.currentTimeMillis()) - lastPull.get() > pullInterval);
    }
    
    private void pullApi() {
        if (apiThread == null) return;

        try {
            fetch();
            module.update();
        } catch (IOException ex) {
            System.out.println(ex);
            MirrorViewController.putAlert("THERE WAS AN ISSUE PULLING FROM THE " + module.getName().toString().replace("_", " ") + " API");
        }
        finally {
            lastPull.set(System.currentTimeMillis());
        }
    }
    
    abstract protected void fetch() throws IOException;

    public void start() {
        if (apiThread != null) return;
        
        apiThread = new Thread(() -> {
            while(true){
                pullApi();
                try {
                    Thread.sleep(pullInterval);
                } catch (InterruptedException ex) {
                    break;
                }
            }
        });
        apiThread.start();
    }
    
    public void stop() {
        if (apiThread == null) return;
        
        apiThread.interrupt();
        apiThread = null;
    }
    
    public void setModule(Module module) {
        this.module = module;
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(PULL_PROP)) {
            pullApi();
        }
    }
}
