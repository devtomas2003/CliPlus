package Classes;

import java.io.Serializable;

public class Address implements Serializable {

    private String Nstreet;
    private int ndoor;
    private int ZipCode;
    private String Nlocality;

    public Address(String Nstreet, int ndoor, int ZipCode, String Nlocality){
        this.Nstreet = Nstreet;
        this.ndoor = ndoor;
        this.ZipCode = ZipCode;
        this.Nlocality = Nlocality;
    }

    public String getNstreet() {
        return Nstreet;
    }

    public void setNstreet(String Nstreet) {
        Nstreet = Nstreet;
    }

    public int getndoor() {
        return ndoor;
    }

    public void setndoor(int ndoor) {
        this.ndoor = ndoor;
    }

    public int getZipCode() {
        return ZipCode;
    }

    public void setZipCode(int ZipCode) {
        ZipCode = ZipCode;
    }

    public String getNlocality() {
        return Nlocality;
    }

    public void setNlocality(String Nlocality) {
        Nlocality = Nlocality;
    }

    @Override
    public String toString() {
        return
            "Street Details:\n" +
                "Street: " + Nstreet + "\n" +
                "Door: " + ndoor + "\n" +
                "Zip Code: " + ZipCode + "\n" +
                "Locality: " + Nlocality;
    }
}
