/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import smartmirror.SmartMirror;


/**
 *
 * @author hasan
 */
public class Watcher extends Thread {
    private Path path;
    private WatchService watcher;
    private Map<WatchKey, Path> keys;

    public Watcher(){
        try {
            path = Paths.get("resources");
            watcher = FileSystems.getDefault().newWatchService();
            keys = new HashMap<>();

            WatchKey myKey = path.register(watcher, ENTRY_MODIFY);
            keys.put(myKey, path);
        } catch (NoSuchFileException ex) {
            System.err.println("WATCHER NOT RUNNING IN DEVELOPMENT ENVIRONMENT");
        } catch (IOException ex) {
            Logger.getLogger(Watcher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>)event;
    }

    @Override
    public void run(){
        while (true){
            WatchKey key;
            try{
                key = watcher.take();
                
                
                Path dir = keys.get(key);
                if (dir == null) {
                    System.err.println("WatchKey not recognized!!");
                    System.exit(0);
                }

                key.pollEvents().forEach((event) -> {
                    WatchEvent.Kind kind = event.kind();
                    WatchEvent<Path> ev = cast(event);
                    Path name = ev.context();
                    Path child = dir.resolve(name);
                    if (kind == ENTRY_MODIFY && 
                            child.toString().equals("resources/mirror_config.xml")) {
                        PCS.INST.firePropertyChange(PCM.NEW_CONFIG);
                    }
                });

                // Do this to continue processing events
                key.reset();
            }
            catch (InterruptedException ex){
                System.err.println("Watcher was interrupted");
            }
        } 
    }
}
