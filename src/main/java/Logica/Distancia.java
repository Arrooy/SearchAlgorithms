package Logica;

public class Distancia {

    private GraphNode origen;
    private GraphNode desti;
    private Integer distancia;
    private Integer duracio;

    public Distancia(GraphNode origen, GraphNode desti, Integer distancia, Integer duracio) {
        this.origen = origen;
        this.desti = desti;
        this.distancia = distancia;
        this.duracio = duracio;
    }

    public GraphNode getOrigen() {
        return origen;
    }

    public void setOrigen(GraphNode origen) {
        this.origen = origen;
    }

    public GraphNode getDesti() {
        return desti;
    }

    public void setDesti(GraphNode desti) {
        this.desti = desti;
    }

    public Integer getDistancia() {
        return distancia;
    }

    public void setDistancia(Integer distancia) {
        this.distancia = distancia;
    }

    public Integer getDuracio() {
        return duracio;
    }

    public void setDuracio(Integer duracio) {
        this.duracio = duracio;
    }
}
