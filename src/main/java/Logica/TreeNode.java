package Logica;

import Model.City;

import java.util.LinkedList;

public class TreeNode {

    private final double parentDistance;
    private double g;
    private double h;
    private City city;
    private final LinkedList<Distancia> connexions;

    private TreeNode parent;

    public TreeNode(City city) {
        this.g = this.h = 0;
        this.city = city;
        connexions = new LinkedList<>();

        this.parent = null;
        this.parentDistance = 0;
    }

    public TreeNode(City city,LinkedList<Distancia> connexions , double g, double h, TreeNode parent, double parentDistance) {
        this.g = g;
        this.h = h;
        this.city = city;
        this.connexions = connexions;
        this.parent = parent;
        this.parentDistance = parentDistance;
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

    public TreeNode getParent() {
        return parent;
    }
}