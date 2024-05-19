package Utils;

import Classes.*;

import java.io.*;
import java.util.ArrayList;

public class FilesDAT {
    public static ArrayList<Appointment> readAppointments() {
        ArrayList<Appointment> result = new ArrayList<Appointment>();
        try {
            File f = new File("appointments.dat");
            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream ois = new ObjectInputStream(fis);
            while (fis.available() > 0) {
                try {
                    Appointment obj;
                    obj = (Appointment) ois.readObject();
                    result.add(obj);

                } catch (ClassNotFoundException error) {
                    System.out.println("Error Message (ClassNotFoundException): " + error.getMessage());
                    error.printStackTrace();
                }
            }
            fis.close();
            ois.close();

        } catch (IOException error) {
            System.out.println("Error Message (IOException): " + error.getMessage());
            error.printStackTrace();
        }
        return result;
    }
    public static void writerAppointments(ArrayList<Appointment> list) {
        try {
            File f = new File("appointments.dat");
            FileOutputStream fos = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            for (Appointment obj: list) {
                oos.writeObject(obj);
            }

            fos.close();
            oos.close();
        } catch (IOException error) {
            System.out.println("Error Message: " + error.getMessage());
            error.printStackTrace();
        }
    }

    public static void writerPeoples(ArrayList<People> list) {
        try {
            File f = new File("people.dat");
            FileOutputStream fos = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            for (People obj: list) {
                oos.writeObject(obj);
            }

            fos.close();
            oos.close();
        } catch (IOException error) {
            System.out.println("Error Message: " + error.getMessage());
            error.printStackTrace();
        }
    }
    public static ArrayList<People> readPeoples() {
        ArrayList<People> result = new ArrayList<People>();
        try {
            File f = new File("people.dat");
            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream ois = new ObjectInputStream(fis);
            while (fis.available() > 0) {
                try {
                    People obj;
                    obj = (People) ois.readObject();
                    result.add(obj);

                } catch (ClassNotFoundException error) {
                    System.out.println("Error Message (ClassNotFoundException): " + error.getMessage());
                    error.printStackTrace();
                }
            }
            fis.close();
            ois.close();

        } catch (IOException error) {
            System.out.println("Error Message (IOException): " + error.getMessage());
            error.printStackTrace();
        }
        return result;
    }
}