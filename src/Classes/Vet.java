package Classes;

public class Vet extends People {
    private int idOV;
    public Vet(int Nif, String name, int idOV, String Contact){
        super(Nif, name, Contact);
        this.idOV = idOV;
    }

    public int getIdOV() {
        return idOV;
    }

    public void setIdOV(int idOV) {
        this.idOV = idOV;
    }

    @Override
    public String toString() {
        return
            "Vet Details:\n" +
                "OMV ID: " + idOV;
    }
}
