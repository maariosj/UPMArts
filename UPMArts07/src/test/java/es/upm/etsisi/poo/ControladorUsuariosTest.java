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
 * Pruebas unitarias para ControladorUsuarios (RegistroParticipantes solo).
 * Cubren caja negra (entradas/salidas) y caja blanca (caminos/decisiones).
 */
public class ControladorUsuariosTest {

    private FakeUsuarioDAO dao;
    private FakeAutenticador autenticador;
    private ControladorUsuarios controlador;

    @BeforeEach
    void setUp() throws IOException {

        dao = new FakeUsuarioDAO();
        autenticador = new FakeAutenticador();
        controlador = new ControladorUsuarios(dao, autenticador);

        SesionActiva.getInstancia().cerrarSesion();
    }

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
    void registrarParticipante_upm_pdi_correcto_guardaPersonalUPM() {
        autenticador.setRolADevolver(RolUPM.PDI);

        HashMap<String, String> datos = datosParticipanteUPMBase("NickPdi", "Dr. Genio", "profesor@upm.es", "Abcdefghij1K", "87654321B", "4111111111111111");
        datos.put("antiguedad", "5");

        assertTrue(controlador.registrarParticipante(datos));
        Usuario u = dao.buscarPorEmail("profesor@upm.es");
        assertNotNull(u);
        assertTrue(u instanceof PersonalUPM);
        assertEquals(5, ((PersonalUPM) u).getAntiguedad());
    }

    @Test
    void registrarParticipante_upm_estudiante_conDominioAlumnos_correcto() {
        autenticador.setRolADevolver(RolUPM.ESTUDIANTE);

        HashMap<String, String> datos = datosParticipanteUPMBase("AlumnoS", "Luis Lopez", "l.lopez@alumnos.upm.es", "Abcdefghij1K", "11223344C", "4111111111111111");
        datos.put("matricula", "M5678");

        assertTrue(controlador.registrarParticipante(datos));
        Usuario u = dao.buscarPorEmail("l.lopez@alumnos.upm.es");
        assertNotNull(u);
        assertTrue(u instanceof EstudianteUPM);
    }

    @Test
    void registrarParticipante_upm_rolInexistente_devuelveFalse() {
        autenticador.setRolADevolver(RolUPM.INEXISTENTE);

        HashMap<String, String> datos = datosParticipanteUPMBase("NickOk", "Nombre", "x@upm.es", "Abcdefghij1K", "12345678A", "4111111111111111");
        assertFalse(controlador.registrarParticipante(datos));
        assertNull(dao.buscarPorEmail("x@upm.es"));
    }

    @Test
    void registrarParticipante_upm_pas_antiguedadNoNumerica_devuelveFalse() {
        autenticador.setRolADevolver(RolUPM.PDI);

        HashMap<String, String> datos = datosParticipanteUPMBase("NickOk", "Nombre", "pdi@upm.es", "Abcdefghij1K", "12345678A", "4111111111111111");
        datos.put("antiguedad", "tres");

        assertFalse(controlador.registrarParticipante(datos));
    }

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

    static class FakeAutenticador implements IAdaptadorAutenticador {
        private RolUPM rolADevolver = RolUPM.INEXISTENTE;
        void setRolADevolver(RolUPM rol) { this.rolADevolver = rol; }
        @Override
        public RolUPM validarEnUPM(String email) { return rolADevolver; }
    }
}
