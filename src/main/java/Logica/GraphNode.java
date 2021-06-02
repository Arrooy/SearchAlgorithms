package Logica;

import Model.City;

import java.util.LinkedList;

public class GraphNode {

    private final City city;
    private final LinkedList<Distancia> connexions;

    private double g;
    private double h;

    private GraphNode parent;

    public GraphNode(City city) {
        this.g = this.h = 0;
        this.city = city;
        connexions = new LinkedList<>();
        this.parent = null;
    }

    public void addDist(Distancia distancia) {
        connexions.add(distancia);
    }

    public City getCity() {
        return city;
    }
    public LinkedList<Distancia> getConnexions() {
        return this.connexions;
    }
    public double getG() {
        return g;
    }
    public void setG(double x) {
        g = x;
    }
    public double getH() {
        return h;
    }
    public void setH(double x) {
        h = x;
    }

    public GraphNode getParent() {
        return parent;
    }
    public void setParent(GraphNode parent) {
        this.parent = parent;
    }

    public String key() {
        return getCity().getName();
    }
}