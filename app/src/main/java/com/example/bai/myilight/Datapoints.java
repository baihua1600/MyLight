package com.example.bai.myilight;

public class Datapoints {
    private String x_name;
    private int y_state;
    private double z_v;

    public Datapoints(String x_name, int y_state, double z_v) {
        super();
        this.x_name = x_name;
        this.y_state = y_state;
        this.z_v = z_v;
    }

    public String getX_name() {
        return x_name;
    }

    public int getY_state() {
        return y_state;
    }

    public double getZ_v() {
        return z_v;
    }
}
