package Classes;

import java.io.Serializable;

public class People implements Serializable {
    private int nif;
    private String name;
    private String contact;
    private Address address;

    public int getNif() {
        return nif;
    }

    public void setNif(int nif) {
        this.nif = nif;
    }

    public String getName() {
        return name;
    }

    public void setNome(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContato(String contact) {
        this.contact = contact;
    }

    public People(int nif, String name, String contato) {
        this.nif = nif;
        this.name = name;
        this.contact = contato;
    }
    public Address getAddress() {
        return address;
    }
    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return
            "Client Details:\n" +
            "NIF: " + nif + "\n" +
            "Nome: " + name + "\n" +
            "Contato: " + contact + "\n" +
            address.toString();
    }
}
