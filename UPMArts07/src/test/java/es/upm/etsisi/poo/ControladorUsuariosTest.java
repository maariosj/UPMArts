package es.upm.etsisi.poo;

import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.HashMap;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para ControladorUsuarios (alta y acceso de usuarios).
 * Cubren caja negra (entradas/salidas) y caja blanca (caminos/decisiones).
 */
public class ControladorUsuariosTest {

    //private static final Path CONFLICTIVOS = Paths.get("conflictivos.txt");

    private FakeUsuarioDAO dao;
    private FakeAutenticador autenticador;
    private ControladorUsuarios controlador;

    @BeforeEach
    void setUp() throws IOException {
        // Aseguramos fichero de términos conflictivos para que ValidadorNick sea determinista

       /* Files.write(CONFLICTIVOS, "badnick\nadmin\n".getBytes(),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);*/


        dao = new FakeUsuarioDAO();
        autenticador = new FakeAutenticador();
        controlador = new ControladorUsuarios(dao, autenticador);

        SesionActiva.getInstancia().cerrarSesion();
    }

    /*@AfterEach
    void tearDown() throws IOException {
        SesionActiva.getInstancia().cerrarSesion();
        try {
            Files.deleteIfExists(CONFLICTIVOS);
        } catch (IOException ignored) {
        }
    }*/

    // ---------------------- validarContrasena ----------------------

    @Test
    void validarContrasena_null_devuelveFalse() {
        assertFalse(controlador.validarContrasena(null));
    }

    @Test
    void validarContrasena_menosDe12_devuelveFalse() {
        assertFalse(controlador.validarContrasena("Aa1"));
    }

    @Test
    void validarContrasena_sinMayus_devuelveFalse() {
        assertFalse(controlador.validarContrasena("abcdefg12345"));
    }

    @Test
    void validarContrasena_sinMinus_devuelveFalse() {
        assertFalse(controlador.validarContrasena("ABCDEFG12345"));
    }

    @Test
    void validarContrasena_sinNumero_devuelveFalse() {
        assertFalse(controlador.validarContrasena("AbcdefghijkL"));
    }

    @Test
    void validarContrasena_valida_devuelveTrue() {
        assertTrue(controlador.validarContrasena("Abcdefghij1K"));
    }

    // ---------------------- validarNick ----------------------

    @Test
    void validarNick_null_devuelveFalse() {
        assertFalse(controlador.validarNick(null));
    }

    @Test
    void validarNick_longitudFueraDeRango_devuelveFalse() {
        assertFalse(controlador.validarNick("abc")); // <4
        assertFalse(controlador.validarNick("abcdefghijklmn")); // >12
    }

    @Test
    void validarNick_conCaracterNoAlfanumerico_devuelveFalse() {
        assertFalse(controlador.validarNick("ab-cd"));
    }

    @Test
    void validarNick_enListaConflictivos_devuelveFalse() {
        assertFalse(controlador.validarNick("ADMIN")); // case-insensitive
    }

    @Test
    void validarNick_correcto_devuelveTrue() {
        assertTrue(controlador.validarNick("Nick123"));
    }

    // ---------------------- determinarTipoMiembroUPM ----------------------

    @Test
    void determinarTipoMiembroUPM_delegaEnAutenticador() {
        autenticador.setRolADevolver(RolUPM.PAS);
        assertEquals(RolUPM.PAS, controlador.determinarRolUPM("alguien@upm.es"));
    }

    // ---------------------- login ----------------------

    @Test
    void login_usuarioNoExiste_devuelveFalse_yNoCreaSesion() {
        assertFalse(controlador.login("no@upm.es", "Abcdefghij1K"));
        assertNull(SesionActiva.getInstancia().getUsuario());
    }

    @Test
    void login_contrasenaIncorrecta_devuelveFalse_yNoCreaSesion() {
        // Creamos un usuario en el DAO
        Usuario u = new Instructor("nick1", "Nombre", "user@dom.com", "Abcdefghij1K", "123", "ES00");
        dao.guardarUsuario(u);

        assertFalse(controlador.login("user@dom.com", "OtraContrasena1A"));
        assertNull(SesionActiva.getInstancia().getUsuario());
    }

    @Test
    void login_correcto_devuelveTrue_yGuardaSesion() {
        Usuario u = new Instructor("nick1", "Nombre", "user@dom.com", "Abcdefghij1K", "123", "ES00");
        dao.guardarUsuario(u);

        assertTrue(controlador.login("user@dom.com", "Abcdefghij1K"));
        assertNotNull(SesionActiva.getInstancia().getUsuario());
        assertEquals("nick1", SesionActiva.getInstancia().getUsuario().getNick());
    }

    // ---------------------- registrarInstructor ----------------------

    @Test
    void registrarInstructor_nickInvalido_devuelveFalse() {
        HashMap<String, String> datos = datosBasicos("bad", "Nombre", "inst@dom.com", "Abcdefghij1K", "999");
        HashMap<String, String> pago = new HashMap<>();
        pago.put("iban", "ES00");

        assertFalse(controlador.registrarInstructor(datos, pago));
    }

    @Test
    void registrarInstructor_contrasenaInvalida_devuelveFalse() {
        HashMap<String, String> datos = datosBasicos("NickOk", "Nombre", "inst@dom.com", "corta", "999");
        HashMap<String, String> pago = new HashMap<>();
        pago.put("iban", "ES00");

        assertFalse(controlador.registrarInstructor(datos, pago));
    }

    @Test
    void registrarInstructor_emailDuplicado_devuelveFalse() {
        // Preexistente
        dao.guardarUsuario(new Instructor("x", "y", "dup@dom.com", "Abcdefghij1K", "1", "ES00"));

        HashMap<String, String> datos = datosBasicos("NickOk", "Nombre", "dup@dom.com", "Abcdefghij1K", "999");
        HashMap<String, String> pago = new HashMap<>();
        pago.put("iban", "ES00");

        assertFalse(controlador.registrarInstructor(datos, pago));
    }

    @Test
    void registrarInstructor_nickDuplicado_devuelveFalse() {
        dao.guardarUsuario(new Instructor("NickOk", "y", "a@dom.com", "Abcdefghij1K", "1", "ES00"));

        HashMap<String, String> datos = datosBasicos("NickOk", "Nombre", "nuevo@dom.com", "Abcdefghij1K", "999");
        HashMap<String, String> pago = new HashMap<>();
        pago.put("iban", "ES00");

        assertFalse(controlador.registrarInstructor(datos, pago));
    }

    @Test
    void registrarInstructor_correcto_guardaInstructor_yDevuelveTrue() {
        HashMap<String, String> datos = datosBasicos("NickOk", "Nombre", "inst@dom.com", "Abcdefghij1K", "999");
        HashMap<String, String> pago = new HashMap<>();
        pago.put("iban", "ES00");

        assertTrue(controlador.registrarInstructor(datos, pago));
        assertNotNull(dao.buscarPorEmail("inst@dom.com"));
        assertTrue(dao.buscarPorEmail("inst@dom.com") instanceof Instructor);
    }

    // ---------------------- registrarParticipante ----------------------

    @Test
    void registrarParticipante_nickInvalido_devuelveFalse() {
        HashMap<String, String> datos = datosParticipanteExterno("bad", "Nombre", "p@dom.com", "Abcdefghij1K", "12345678A", "4111111111111111");
        assertFalse(controlador.registrarParticipante(datos));
    }

    @Test
    void registrarParticipante_contrasenaInvalida_devuelveFalse() {
        HashMap<String, String> datos = datosParticipanteExterno("NickOk", "Nombre", "p@dom.com", "corta", "12345678A", "4111111111111111");
        assertFalse(controlador.registrarParticipante(datos));
    }

    @Test
    void registrarParticipante_emailDuplicado_devuelveFalse() {
        dao.guardarUsuario(new ParticipanteExterno("a", "b", "dup@dom.com", "Abcdefghij1K", "1", "2"));
        HashMap<String, String> datos = datosParticipanteExterno("NickOk", "Nombre", "dup@dom.com", "Abcdefghij1K", "12345678A", "4111111111111111");
        assertFalse(controlador.registrarParticipante(datos));
    }

    @Test
    void registrarParticipante_nickDuplicado_devuelveFalse() {
        dao.guardarUsuario(new ParticipanteExterno("NickOk", "b", "x@dom.com", "Abcdefghij1K", "1", "2"));
        HashMap<String, String> datos = datosParticipanteExterno("NickOk", "Nombre", "nuevo@dom.com", "Abcdefghij1K", "12345678A", "4111111111111111");
        assertFalse(controlador.registrarParticipante(datos));
    }

    @Test
    void registrarParticipante_externo_correcto_guardaParticipanteExterno() {
        HashMap<String, String> datos = datosParticipanteExterno("NickOk", "Nombre", "p@dom.com", "Abcdefghij1K", "12345678A", "4111111111111111");
        assertTrue(controlador.registrarParticipante(datos));
        Usuario u = dao.buscarPorEmail("p@dom.com");
        assertNotNull(u);
        assertTrue(u instanceof ParticipanteExterno);
    }

    @Test
    void registrarParticipante_upm_estudiante_correcto_guardaEstudianteUPM() {
        autenticador.setRolADevolver(RolUPM.ESTUDIANTE);

        HashMap<String, String> datos = datosParticipanteUPMBase("NickOk", "Nombre", "alumno@upm.es", "Abcdefghij1K", "12345678A", "4111111111111111");
        datos.put("matricula", "M1234");

        assertTrue(controlador.registrarParticipante(datos));
        Usuario u = dao.buscarPorEmail("alumno@upm.es");
        assertNotNull(u);
        assertTrue(u instanceof EstudianteUPM);
        assertEquals("M1234", ((EstudianteUPM) u).getMatricula());
    }

    @Test
    void registrarParticipante_upm_pas_o_pdi_correcto_guardaPersonalUPM() {
        autenticador.setRolADevolver(RolUPM.PAS);

        HashMap<String, String> datos = datosParticipanteUPMBase("NickOk", "Nombre", "pas@upm.es", "Abcdefghij1K", "12345678A", "4111111111111111");
        datos.put("antiguedad", "3");

        assertTrue(controlador.registrarParticipante(datos));
        Usuario u = dao.buscarPorEmail("pas@upm.es");
        assertNotNull(u);
        assertTrue(u instanceof PersonalUPM);
        assertEquals(3, ((PersonalUPM) u).getAntiguedad());
    }

    @Test
    void registrarParticipante_upm_rolInexistente_devuelveFalse() {
        autenticador.setRolADevolver(RolUPM.INEXISTENTE);

        HashMap<String, String> datos = datosParticipanteUPMBase("NickOk", "Nombre", "x@upm.es", "Abcdefghij1K", "12345678A", "4111111111111111");
        assertFalse(controlador.registrarParticipante(datos));
        assertNull(dao.buscarPorEmail("x@upm.es"));
    }

    @Test
    void registrarParticipante_upm_pas_antiguedadNoNumerica_lanzaExcepcion() {
        // Caja blanca: hay un Integer.parseInt sin control de excepción.
        autenticador.setRolADevolver(RolUPM.PDI);

        HashMap<String, String> datos = datosParticipanteUPMBase("NickOk", "Nombre", "pdi@upm.es", "Abcdefghij1K", "12345678A", "4111111111111111");
        datos.put("antiguedad", "tres");

        assertThrows(NumberFormatException.class, () -> controlador.registrarParticipante(datos));
    }

    // ---------------------- Helpers y dobles de prueba ----------------------

    private static HashMap<String, String> datosBasicos(String nick, String nombre, String correo, String contrasena, String dni) {
        HashMap<String, String> datos = new HashMap<>();
        datos.put("nick", nick);
        datos.put("nombre", nombre);
        datos.put("correo", correo);
        datos.put("contrasena", contrasena);
        datos.put("dni", dni);
        return datos;
    }

    private static HashMap<String, String> datosParticipanteExterno(String nick, String nombre, String correo, String contrasena, String dni, String tarjeta) {
        HashMap<String, String> datos = datosBasicos(nick, nombre, correo, contrasena, dni);
        datos.put("tarjetaCredito", tarjeta);
        return datos;
    }

    private static HashMap<String, String> datosParticipanteUPMBase(String nick, String nombre, String correoUpm, String contrasena, String dni, String tarjeta) {
        return datosParticipanteExterno(nick, nombre, correoUpm, contrasena, dni, tarjeta);
    }

    /** Fake DAO en memoria para aislar el controlador de I/O (ficheros). */
    static class FakeUsuarioDAO implements IUsuarioDAO {
        private final java.util.Map<String, Usuario> porEmail = new HashMap<>();
        private final java.util.Map<String, Usuario> porNick = new HashMap<>();

        @Override
        public boolean guardarUsuario(Usuario u) {
            if (u == null || existeNick(u.getNick()) || buscarPorEmail(u.getCorreo()) != null) return false;
            porEmail.put(u.getCorreo(), u);
            porNick.put(u.getNick(), u);
            return true;
        }

        @Override
        public Usuario buscarPorEmail(String email) {
            return porEmail.get(email);
        }

        @Override
        public boolean existeNick(String nick) {
            return porNick.containsKey(nick);
        }

        @Override
        public boolean eliminarUsuario(String nick) {
            Usuario u = porNick.remove(nick);
            if (u == null) return false;
            porEmail.remove(u.getCorreo());
            return true;
        }

        @Override
        public boolean actualizarUsuario(String nick, Usuario usuarioNuevo) {
            if (!porNick.containsKey(nick) || usuarioNuevo == null) return false;
            // eliminamos el anterior
            Usuario anterior = porNick.remove(nick);
            if (anterior != null) porEmail.remove(anterior.getCorreo());
            // guardamos el nuevo
            porNick.put(usuarioNuevo.getNick(), usuarioNuevo);
            porEmail.put(usuarioNuevo.getCorreo(), usuarioNuevo);
            return true;
        }
    }

    /** Fake autenticador para controlar el rol devuelto en tests. */
    static class FakeAutenticador implements IAdaptadorAutenticador {
        private RolUPM rolADevolver = RolUPM.INEXISTENTE;
        void setRolADevolver(RolUPM rol) { this.rolADevolver = rol; }
        @Override
        public RolUPM validarEnUPM(String email) { return rolADevolver; }
    }
}
