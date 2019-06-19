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
    private final long sleepTime;
    private final AtomicLong lastPull = new AtomicLong(0);
    private final AtomicBoolean stop = new AtomicBoolean(true);
    private final String PULL_PROP;

    APIManager(ModuleName name, long sleepTime, String pullProp) {
        this.name = name;
        this.sleepTime = sleepTime;
        this.PULL_PROP = pullProp;
        PCS.INST.addPropertyChangeListener(PULL_PROP, this);
    }

    private boolean doPull() {
        return (System.currentTimeMillis() - lastPull.get() > sleepTime);
    }
    
    private void pullApi() {
        if (stop.get()) return;

        try {
            fetch();
            module.update();
            lastPull.set(System.currentTimeMillis());
        } catch (IOException ex) {
            System.out.println(ex);
            MirrorViewController.putAlert("THERE WAS AN ISSUE PULLING FROM THE " + name.toString().replace("_", " ") + " API");
        }
    }
    
    abstract protected void fetch() throws IOException;

    public void start() {
        if (apiThread != null) return;
        
        apiThread = new Thread(() -> {
            stop.set(false);
            while(doPull() || !stop.get()){   
                pullApi();
                try {
                    Thread.sleep(sleepTime);
                } 
                catch (InterruptedException ex) {
                    break;
                }
            }
        });
        apiThread.start();
    }
    
    public void stop() {
        if (apiThread == null) return;
        
        stop.set(true);
        apiThread.interrupt();
        apiThread = null;
    }
    
    public void setModule(Module module) {
        this.module = module;
    }
    
    public String getName() {
        return (name != null ? name.toString() : "");
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(PULL_PROP)) {
            pullApi();
        }
    }
}
