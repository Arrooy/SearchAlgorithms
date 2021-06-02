package Logica;

public class Distancia {

    private TreeNode origen;
    private TreeNode desti;
    private Integer distancia;
    private Integer duracio;

    public Distancia(TreeNode origen, TreeNode desti, Integer distancia, Integer duracio) {
        this.origen = origen;
        this.desti = desti;
        this.distancia = distancia;
        this.duracio = duracio;
    }

    public TreeNode getOrigen() {
        return origen;
    }

    public void setOrigen(TreeNode origen) {
        this.origen = origen;
    }

    public TreeNode getDesti() {
        return desti;
    }

    public void setDesti(TreeNode desti) {
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
