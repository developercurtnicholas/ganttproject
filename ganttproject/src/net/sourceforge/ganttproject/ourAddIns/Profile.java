package net.sourceforge.ganttproject.ourAddIns;

import java.util.ArrayList;

/**
 * Created by curt on 3/24/17.
 */
public class Profile {

    private String name;
    private String phone;
    private String mail;
    private String role;
    private String pic;
    private int completionRate = 0;


    private ArrayList<Task> tasks = new ArrayList<>();

    public Profile(String n,String ph,String m,String r,String pic){

        if(n == null){
            this.name = "No name assigned yet";
        }else if(n.equals("")){
            this.name = "No name assigned yet";
        }else{
            this.name = n;
        }

        if(ph == null){
            this.phone = "No phone assigned yet";
        }else if(ph.equals("")){
            this.phone = "No phone assigned yet";
        }else{
            this.phone = ph;
        }

        if(m == null){
            this.mail = "No mail assigned yet";
        }else if(m.equals("")){
            this.mail = "No mail assigned yet";
        }else{
            this.mail = m;
        }

        if(r == null){
            this.role = "No role assigned yet";
        }else if(r.equals("")){
            this.role = "No role assigned yet";
        }else{
            this.role = r;
        }

        this.pic = pic;
    }

    public void addTask(net.sourceforge.ganttproject.task.Task t){

        String p = t.getPriority().getLowerString();
        if(p.equalsIgnoreCase("low") || p.equalsIgnoreCase("lowest")){
            tasks.add(new Low(t));
        }
        if(p.equalsIgnoreCase("high") || p.equalsIgnoreCase("highest")){
            tasks.add(new High(t));
        }
        if(p.equalsIgnoreCase("normal")){
            tasks.add(new Normal(t));
        }
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getMail() {
        return mail;
    }

    public String getRole() {
        return role;
    }

    public String getPic() {
        return pic;
    }

    public Profile setPic(String path){
        this.pic = path;
        return this;
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public int calculateCompletionRate(){

        int total = 0;
        int size = tasks.size();
        if(size > 0){
            for(Task t : tasks){
                total += t.getProgress();
            }

            completionRate = total/size;
        }

        return completionRate;
    }
}
