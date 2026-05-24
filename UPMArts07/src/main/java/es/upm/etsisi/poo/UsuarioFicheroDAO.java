package es.upm.etsisi.poo;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioFicheroDAO implements IUsuarioDAO {

    private String rutaFichero;

    public UsuarioFicheroDAO() {
        this.rutaFichero = "usuarios.txt";
        asegurarExistenciaDeFichero();
    }

    public UsuarioFicheroDAO(String rutaFichero) {
        this.rutaFichero = rutaFichero;
        asegurarExistenciaDeFichero();
    }

    private void asegurarExistenciaDeFichero() {
        File archivo = new File(this.rutaFichero);
        if (!archivo.exists()) {
            try {
                archivo.createNewFile();
            } catch (IOException e) {
                System.err.println("Error crítico: No se pudo crear el almacén de datos de usuarios: " + e.getMessage());
            }
        }
    }

    private String serializar(Usuario u) {
        return u.toString();
    }

    /*
    public Usuario deserializar(String linea) {
        String[] partes = linea.split(";");

        if (partes.length < 5) {
            return null;
        }

        String tipo = partes[0];

        if (tipo.equals("EXTERNO")) {
            if (partes.length < 7) {
                return null;
            }

            String nick = partes[1];
            String nombre = partes[2];
            String correo = partes[3];
            String contrasenaCifrada = partes[4];
            String dni = partes[5];
            String tarjetaCredito = partes[6];

            return new ParticipanteExterno(
                    nick,
                    nombre,
                    correo,
                    contrasenaCifrada,
                    dni,
                    tarjetaCredito
            );
        }

        if (tipo.equals("INSTRUCTOR")) {
            if (partes.length < 7) {
                return null;
            }

            String nick = partes[1];
            String nombre = partes[2];
            String correo = partes[3];
            String contrasenaCifrada = partes[4];
            String dni = partes[5];
            String iban = partes[6];

            Instructor instructor = new Instructor(
                    nick,
                    nombre,
                    correo,
                    contrasenaCifrada,
                    true
            );

            instructor.setDni(dni);
            instructor.setIban(iban);

            return instructor;
        }

        return null;
    }
     */

    public Usuario deserializar(String linea) {
        String[] partes = linea.split(";");

        if (partes.length < 5) {
            return null;
        }

        String tipo = partes[0];
        String nick = partes[1];
        String nombre = partes[2];
        String correo = partes[3];
        String contrasenaCifrada = partes[4];

        switch (tipo) {
            case "EXTERNO":
                if (partes.length >= 7) {
                    return new ParticipanteExterno(nick, nombre, correo, contrasenaCifrada, true, partes[5], partes[6]);
                }
                break;

            case "ESTUDIANTE":
                if (partes.length >= 8) {
                    return new EstudianteUPM(nick, nombre, correo, contrasenaCifrada, true, partes[5], partes[6], partes[7]);
                }
                break;

            case "PERSONAL":
                if (partes.length >= 8) {
                    return new PersonalUPM(nick, nombre, correo, contrasenaCifrada, true, partes[5], partes[6], Integer.parseInt(partes[7]));
                }
                break;

            case "INSTRUCTOR":
                if (partes.length >= 7) {
                    Instructor instructor = new Instructor(nick, nombre, correo, contrasenaCifrada, true);
                    instructor.setDni(partes[5]);
                    instructor.setIban(partes[6]);
                    return instructor;
                }
                break;

            case "ADMINISTRADOR":
                if (partes.length >= 6) {
                    return new Administrador(nick, nombre, correo, contrasenaCifrada, true, partes[5]);
                }
                break;
        }

        return null;
    }

    @Override
    public boolean guardarUsuario(Usuario u) {
        if (u == null || existeNick(u.getNick()) || buscarPorEmail(u.getCorreo()) != null) {
            return false;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(rutaFichero, true))) {
            bw.write(serializar(u));
            bw.newLine();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public Usuario buscarPorEmail(String email) {
        try (BufferedReader br = new BufferedReader(new FileReader(rutaFichero))) {
            String linea;

            while ((linea = br.readLine()) != null) {
                Usuario u = deserializar(linea);

                if (u != null && u.getCorreo().equals(email)) {
                    return u;
                }
            }

        } catch (IOException e) {
            return null;
        }

        return null;
    }

    @Override
    public boolean existeNick(String nick) {
        try (BufferedReader br = new BufferedReader(new FileReader(rutaFichero))) {
            String linea;

            while ((linea = br.readLine()) != null) {
                Usuario u = deserializar(linea);

                if (u != null && u.getNick().equals(nick)) {
                    return true;
                }
            }

        } catch (IOException e) {
            return false;
        }

        return false;
    }

    @Override
    public boolean eliminarUsuario(String nick) {
        List<Usuario> usuarios = new ArrayList<>();
        boolean encontrado = false;

        try (BufferedReader br = new BufferedReader(new FileReader(rutaFichero))) {
            String linea;

            while ((linea = br.readLine()) != null) {
                Usuario u = deserializar(linea);

                if (u != null) {
                    if (u.getNick().equals(nick)) {
                        encontrado = true;
                    } else {
                        usuarios.add(u);
                    }
                }
            }

        } catch (IOException e) {
            return false;
        }

        if (!encontrado) {
            return false;
        }

        return sobrescribirFichero(usuarios);
    }

    @Override
    public boolean actualizarUsuario(String nick, Usuario usuarioNuevo) {
        List<Usuario> usuarios = new ArrayList<>();
        boolean encontrado = false;

        try (BufferedReader br = new BufferedReader(new FileReader(rutaFichero))) {
            String linea;

            while ((linea = br.readLine()) != null) {
                Usuario u = deserializar(linea);

                if (u != null) {
                    if (u.getNick().equals(nick)) {
                        usuarios.add(usuarioNuevo);
                        encontrado = true;
                    } else {
                        usuarios.add(u);
                    }
                }
            }

        } catch (IOException e) {
            return false;
        }

        if (!encontrado) {
            return false;
        }

        return sobrescribirFichero(usuarios);
    }

    private boolean sobrescribirFichero(List<Usuario> usuarios) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(rutaFichero, false))) {
            for (Usuario u : usuarios) {
                bw.write(serializar(u));
                bw.newLine();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}