package api_calls;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import module.Module;
import smartmirror.MirrorViewController;

public abstract class APIManager {
    
    protected final List<Module> modules = new ArrayList<>();
    private Thread apiThread = null;
    private final long pullInterval;
    private long lastPull = 0;

    public APIManager(long pullInterval) {
        this.pullInterval = pullInterval;
    }

    public void pullApi() {
        if (apiThread == null) return;

        try {
            fetch();
            modules.forEach((m) -> m.update());
        } 
        catch (IOException ex) {
            System.out.println(ex);
            MirrorViewController.putAlert("AN ERROR OCCURED PULLING AN API, CHECK THE LOG");
        }
    }
    
    abstract protected void fetch() throws IOException;

    public void start() {
        if (apiThread != null) return;
        
        apiThread = new Thread(() -> {
            while(true){
                if ((System.currentTimeMillis() - lastPull) >= pullInterval) {
                    pullApi();
                }
                lastPull = System.currentTimeMillis();
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
    
    public void addModuleSubscriber(Module module) {
        modules.add(module);
    }
    
    public void removeModuleSubscriber(Module module) {
        modules.remove(module);
    }

    public boolean isRunning() {
        return (apiThread != null);
    }
}
