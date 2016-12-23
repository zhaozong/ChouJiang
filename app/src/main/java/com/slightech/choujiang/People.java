package com.slightech.choujiang;

import java.io.Serializable;

/**
 * Created by Rokey on 2016/12/3.
 */

public class People implements Serializable {
    private String name;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public People(String name, String id) {
        this.name = name;
        this.id = id;
    }
}
