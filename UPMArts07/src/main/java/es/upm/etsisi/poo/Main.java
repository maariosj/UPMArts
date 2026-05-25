package es.upm.etsisi.poo;

public class Main {
    public static void main(String[] args) {

        IUsuarioDAO dao = new UsuarioFicheroDAO();

        IAdaptadorAutenticador autenticador = new AdaptadorUPMAuthenticator();

        IControladorUsuarios controladorUsuarios = new ControladorUsuarios(dao, autenticador);


        VistaUsuariosCLI vistaUsuarios = new VistaUsuariosCLI(controladorUsuarios);

        VistaPrincipalCLI vistaPrincipal = new VistaPrincipalCLI(vistaUsuarios);

        vistaPrincipal.iniciarAplicacion();
    }
}