package net.sourceforge.ganttproject.ourAddIns;

import java.io.Serializable;

/**
 * Created by curt on 4/8/17.
 */
public class PictureData implements Serializable{
    String name;
    String path;

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public PictureData(String name, String path){

        this.name = name;
        this.path = path;
    }
}
