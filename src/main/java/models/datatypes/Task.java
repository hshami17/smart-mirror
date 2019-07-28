package models.datatypes;

/**
 *
 * @author saadshami
 */
public class Task {
   
    private long task_id;
    private String task;
    private String dueDate = "";
    
    public Task(String task){
        this.task = task;  
    }
   
    public void setId(Long id) {
        this.task_id = id;
    }
    
    public void setTask(String task) {
        this.task = task;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }
    
    public long getId() {
        return task_id;
    }

    public String getTask() {
        return task;
    }

    public String getDueDate() {
        return dueDate;
    }
    
    @Override
    public String toString(){
        return "Due: " + dueDate + "\n" + "Task: " + task;
    }
}
