package Menus;

import javax.swing.*;

public class Person {
    public static void showMenu(){
        int opcao;

        do {
            opcao = Integer.parseInt(JOptionPane.showInputDialog(null, "Clients Operations\n\n1 - Create Client", "Clients", JOptionPane.INFORMATION_MESSAGE));

            switch (opcao){
                case 1:
                    System.out.println("zsd");
            }
        }while (opcao != 0);
    }
}
