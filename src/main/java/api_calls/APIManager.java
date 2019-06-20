package api_calls;

import utils.PCS;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import module.Module;
import smartmirror.MirrorViewController;

public abstract class APIManager implements PropertyChangeListener {
    
    protected ModuleName name = null;
    protected Module module = null;
    
    private Thread apiThread = null;
    private final long pullInterval;
    private final AtomicLong lastPull = new AtomicLong(0);
    private final AtomicBoolean stop = new AtomicBoolean(true);
    private final String PULL_PROP;

    APIManager(ModuleName name, long pullInterval, String pullProp) {
        this.name = name;
        this.pullInterval = pullInterval;
        this.PULL_PROP = pullProp;
        PCS.INST.addPropertyChangeListener(PULL_PROP, this);
    }

    private boolean doPull() {
        return ((System.currentTimeMillis() / 1000) - lastPull.get() > pullInterval);
    }
    
    private void pullApi() {
        if (stop.get()) return;

        try {
            fetch();
            module.update();
        } catch (IOException ex) {
            System.out.println(ex);
            MirrorViewController.putAlert("THERE WAS AN ISSUE PULLING FROM THE " + name.toString().replace("_", " ") + " API");
        }
        finally {
            lastPull.set(System.currentTimeMillis() / 1000);
        }
    }
    
    abstract protected void fetch() throws IOException;

    public void start() {
        if (apiThread != null) return;
        
        apiThread = new Thread(() -> {
            stop.set(false);
            while(!stop.get()){
                if (doPull()) {
                    pullApi();
                }
            }
        });
        apiThread.start();
    }
    
    public void stop() {
        if (apiThread == null) return;
        
        stop.set(true);
        apiThread = null;
    }
    
    public void setModule(Module module) {
        this.module = module;
    }
    
    public ModuleName getName() {
        return name;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(PULL_PROP)) {
            pullApi();
        }
    }
}
