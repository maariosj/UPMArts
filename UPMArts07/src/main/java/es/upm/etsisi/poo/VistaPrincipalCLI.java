package es.upm.etsisi.poo;

import java.util.Scanner;

/**
 * Vista principal de la aplicacion CLI.
 *
 * Responsabilidad: mostrar opciones generales y delegar en vistas especificas.
 * No conoce DAOs, modelos ni clases de persistencia.
 */
public class VistaPrincipalCLI {
    private final Scanner scanner;
    private final VistaUsuariosCLI vistaUsuarios;

    public VistaPrincipalCLI(VistaUsuariosCLI vistaUsuarios) {
        this.scanner = new Scanner(System.in);
        this.vistaUsuarios = vistaUsuarios;
    }

    public void iniciarAplicacion() {
        boolean salir = false;

        while (!salir) {
            mostrarMenu();
            String opcion = scanner.nextLine().trim();

            switch (opcion) {
                case "1":
                    vistaUsuarios.mostrarMenuPrincipal();
                    break;
                case "0":
                    salir = true;
                    System.out.println("Saliendo de UPMArts...");
                    break;
                default:
                    System.out.println("Opcion no valida. Intentalo de nuevo.");
            }
        }
    }

    public void mostrarMenu() {
        System.out.println();
        System.out.println("===== UPMArts =====");
        System.out.println("1. Gestion de usuarios");
        System.out.println("0. Salir");
        System.out.print("Selecciona una opcion: ");
    }
}