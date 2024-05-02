import Menus.Person;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        int optionSelected;

        do{
            optionSelected = Integer.parseInt(JOptionPane.showInputDialog(null, "Welcome to CliPlus\n\n1 - Clients\n0 - Exit", "Menu", JOptionPane.INFORMATION_MESSAGE));

            switch (optionSelected){
                case 1:
                    Person.showMenu();
            }
        }while (optionSelected != 0);
    }
}