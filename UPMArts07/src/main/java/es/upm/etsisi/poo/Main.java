package es.upm.etsisi.poo;

public class Main {
    public static void main(String[] args) {

        IUsuarioDAO dao = new UsuarioFicheroDAO();

        // Admin de prueba para desarrollo — solo se crea si no existe
        if (dao.buscarPorEmail("admin@upm.es") == null) {
            Administrador admin = new Administrador(
                    "admin",         // nick
                    "Administrador", // nombre
                    "admin@upm.es",  // correo
                    "Admin01234567", // contraseña (≥12 chars, mayús, minús, número)
                    false,           // yaCifrada = false, que la cifre el constructor
                    "600000000"      // teléfono corporativo
            );
            dao.guardarUsuario(admin);
        }

        IAdaptadorAutenticador autenticador = new AdaptadorUPMAuthenticator();
        IControladorUsuarios controladorUsuarios = new ControladorUsuarios(dao, autenticador);
        VistaUsuariosCLI vistaUsuarios = new VistaUsuariosCLI(controladorUsuarios);
        VistaPrincipalCLI vistaPrincipal = new VistaPrincipalCLI(vistaUsuarios);
        vistaPrincipal.iniciarAplicacion();
    }
}