package com.palmer.krawl.SpanningTree;

import com.palmer.krawl.delaunay_triangulation.PointDT;

public class Edge {

    private boolean isReached;
    private PointDT p1;
    private PointDT p2;
    private int weight;

    public Edge(PointDT p1, PointDT p2) {
        this.p1 = p1;
        this.p2 = p2;
        this.isReached = false;
    }

    public boolean isReached() {
        return isReached;
    }

    public void setReached(boolean isReached) {
        this.isReached = isReached;
    }

    public PointDT getP1() {
        return p1;
    }

    public void setP1(PointDT p1) {
        this.p1 = p1;
    }

    public PointDT getP2() {
        return p2;
    }

    public void setP2(PointDT p2) {
        this.p2 = p2;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
