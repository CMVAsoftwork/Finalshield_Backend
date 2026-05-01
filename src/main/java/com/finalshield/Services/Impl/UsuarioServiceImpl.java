package com.finalshield.Services.Impl;

import com.finalshield.DTO.Sesion.LoginBioRequest;
import com.finalshield.DTO.Sesion.LoginRequest;
import com.finalshield.DTO.Sesion.LoginResponse;
import com.finalshield.DTO.Sesion.RegistroRequest;
import com.finalshield.Model.Usuario;
import com.finalshield.Repositorios.UsuarioRepositorio;
import com.finalshield.Security.JwtTokenProvider;
import com.finalshield.Services.CifradorAESService;
import com.finalshield.Services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepositorio RepoUsuario;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private CifradorAESService cifradorAES;
    @Autowired
    private PasswordEncryptorService passwordEncryptor;


    /*@Override
    public LoginResponse registrar(RegistroRequest request) {
        if (RepoUsuario.findByCorreo(request.getCorreo()).isPresent()) {
            throw new RuntimeException("El correo ya está registrado.");
        }

        Usuario nuevo = new Usuario();
        nuevo.setNombre(request.getNombre());
        nuevo.setApellidoP(request.getApellidoP());
        nuevo.setApellidoM(request.getApellidoM());
        nuevo.setCorreo(request.getCorreo());
        nuevo.setContrasena(passwordEncoder.encode(request.getContrasena()));
        nuevo.setHuella(false);

        SecretKey claveAES = cifradorAES.generarClave();
        byte[] claveBytes = cifradorAES.claveABytes(claveAES);

        String salt = KeyGenerators.string().generateKey();
        BytesEncryptor encryptor = Encryptors.stronger(request.getContrasena(), salt);
        byte[] claveCifradaBytes = encryptor.encrypt(claveBytes);

        String claveCifradaBase64 = Base64.getEncoder().encodeToString(claveCifradaBytes);
        String claveFinal = salt + ":" + claveCifradaBase64;
        nuevo.setClaveCifDesPersonal(claveFinal);

        Usuario guardado = RepoUsuario.save(nuevo);
        String token = jwtTokenProvider.generarToken(guardado.getCorreo(), guardado.getNombre());

        byte[] claveAESDescifradaBytes = encryptor.decrypt(claveCifradaBytes);
        String claveAESDescifradaParaClienteBase64 = Base64.getEncoder().encodeToString(claveAESDescifradaBytes);

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setNombre(guardado.getNombre());
        response.setCorreo(guardado.getCorreo());
        response.setClaveCifDesPersonal(claveAESDescifradaParaClienteBase64);
        response.setIdUsuario(guardado.getIdUsuario());

        return response;
    }

    public LoginResponse login(LoginRequest request) {
        Usuario usuario = RepoUsuario.findByCorreo(request.getCorreo()).orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        if (!passwordEncoder.matches(request.getContrasena(), usuario.getContrasena())) {
            throw new RuntimeException("Contraseña incorrecta.");
        }

        String clavePersonalCifradaAlmacenada = usuario.getClaveCifDesPersonal();

        if (clavePersonalCifradaAlmacenada == null || !clavePersonalCifradaAlmacenada.contains(":")) {
            throw new RuntimeException("Clave personal cifrada no disponible o mal formada para el usuario.");
        }

        String[] partes = clavePersonalCifradaAlmacenada.split(":");
        String saltParaDescifrado = partes[0];
        String claveAESCifradaSoloBase64 = partes[1];

        byte[] claveAESCifradaBytes = Base64.getDecoder().decode(claveAESCifradaSoloBase64);

        BytesEncryptor desencriptadorClaveAES = Encryptors.stronger(request.getContrasena(), saltParaDescifrado);
        byte[] claveAESDescifradaBytes = desencriptadorClaveAES.decrypt(claveAESCifradaBytes);
        String claveAESDescifradaParaClienteBase64 = Base64.getEncoder().encodeToString(claveAESDescifradaBytes);

        String token = jwtTokenProvider.generarToken(usuario.getCorreo(), usuario.getNombre());

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setNombre(usuario.getNombre());
        response.setCorreo(usuario.getCorreo());
        response.setIdUsuario(usuario.getIdUsuario());
        response.setClaveCifDesPersonal(claveAESDescifradaParaClienteBase64);

        return response;
    }

    @Override
    public SecretKey recuperarClaveAES(Integer idUsuario, String contrasena) {
        Usuario usuario = RepoUsuario.findById(idUsuario).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        String[] partes = usuario.getClaveCifDesPersonal().split(":");
        BytesEncryptor decryptor = Encryptors.stronger(contrasena, partes[0]);
        try {
            byte[] claveBytes = decryptor.decrypt(Base64.getDecoder().decode(partes[1]));
            return new SecretKeySpec(claveBytes, "AES");
        } catch (Exception e) {
            throw new RuntimeException("No se pudo descifrar la clave AES.");
        }
    }

    @Override
    public Optional<Usuario> obtenerPorCorreo(String correo) {
        return RepoUsuario.findByCorreo(correo);
    }


    @Override
    public void cambiarContrasena(String correo, String contrasenaActual, String nuevaContrasena) {
        Usuario usuario = RepoUsuario.findByCorreo(correo).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(contrasenaActual, usuario.getContrasena())) {
            throw new RuntimeException("Contraseña actual incorrecta");
        }

        String claveCifradaBase64 = usuario.getClaveCifDesPersonal();
        if (claveCifradaBase64 == null || !claveCifradaBase64.contains(":")) {
            throw new RuntimeException("No se encontró una clave cifrada válida.");
        }

        String[] partes = usuario.getClaveCifDesPersonal().split(":");
        BytesEncryptor decryptor = Encryptors.stronger(contrasenaActual, partes[0]);
        byte[] claveAESBytes;
        try {
            claveAESBytes = decryptor.decrypt(Base64.getDecoder().decode(partes[1]));
        } catch (Exception e) {
            throw new RuntimeException("Error crítico: No se pudo recuperar la clave maestra de archivos.");
        }

        String nuevoSalt = KeyGenerators.string().generateKey();
        BytesEncryptor encryptor = Encryptors.stronger(nuevaContrasena, nuevoSalt);
        usuario.setClaveCifDesPersonal(nuevoSalt + ":" + Base64.getEncoder().encodeToString(encryptor.encrypt(claveAESBytes)));
        usuario.setContrasena(passwordEncoder.encode(nuevaContrasena));

        RepoUsuario.save(usuario);
    }

    @Override
    public void recuperarContrasenaSinToken(String correo, String contrasenaActual, String nuevaContrasena) {
        Usuario usuario = RepoUsuario.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        if (!passwordEncoder.matches(contrasenaActual, usuario.getContrasena())) {
            throw new RuntimeException("Contraseña actual incorrecta.");
        }

        String cifrado = usuario.getClaveCifDesPersonal();
        if (cifrado == null || !cifrado.contains(":")) {
            throw new RuntimeException("Clave cifrada inválida.");
        }

        String[] partes = cifrado.split(":");
        String saltAnterior = partes[0];
        String claveAnteriorCifrada = partes[1];

        byte[] claveCifradaBytes = Base64.getDecoder().decode(claveAnteriorCifrada);
        BytesEncryptor decryptor = Encryptors.stronger(contrasenaActual, saltAnterior);
        byte[] claveAESBytes = decryptor.decrypt(claveCifradaBytes);

        String nuevoSalt = KeyGenerators.string().generateKey();
        BytesEncryptor encryptor = Encryptors.stronger(nuevaContrasena, nuevoSalt);
        byte[] nuevaClaveCifrada = encryptor.encrypt(claveAESBytes);
        String nuevaClaveFinal = nuevoSalt + ":" + Base64.getEncoder().encodeToString(nuevaClaveCifrada);

        usuario.setContrasena(passwordEncoder.encode(nuevaContrasena));
        usuario.setClaveCifDesPersonal(nuevaClaveFinal);
        RepoUsuario.save(usuario);
    }

    @Override
    public Boolean isBiometricoActivo(String correo) {
        return RepoUsuario.findByCorreo(correo)
                .map(Usuario::getHuella)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    @Override
    public LoginResponse loginBiometrico(String correo) {
        Usuario usuario = RepoUsuario.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        String clavePersonalCifrada = usuario.getClaveCifDesPersonal();
        String claveAESDescifradaParaClienteBase64 = null;

        if (clavePersonalCifrada != null && clavePersonalCifrada.contains(":")) {
            claveAESDescifradaParaClienteBase64 = clavePersonalCifrada;
        }

        String token = jwtTokenProvider.generarToken(usuario.getCorreo(), usuario.getNombre());

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setCorreo(usuario.getCorreo());
        response.setNombre(usuario.getNombre());
        response.setIdUsuario(usuario.getIdUsuario());
        response.setClaveCifDesPersonal(claveAESDescifradaParaClienteBase64);

        return response;
    }*/

    @Override
    public LoginResponse registrar(RegistroRequest request) {
        if (RepoUsuario.findByCorreo(request.getCorreo()).isPresent()) {
            throw new RuntimeException("El correo ya está registrado.");
        }

        Usuario nuevo = new Usuario();
        nuevo.setNombre(request.getNombre());
        // Se agregaron estos campos para evitar el error 500 de SQL
        nuevo.setCorreo(request.getCorreo());
        nuevo.setContrasena(passwordEncoder.encode(request.getContrasena()));

        // --- GUARDADO DE PINES (ESTO ARREGLA TU ERROR) ---
        nuevo.setPinRealHash(request.getPinReal());
        nuevo.setPinSeguroHash(request.getPinSeguro());

        nuevo.setHuella(false); // Por defecto desactivado hasta que el usuario lo habilite

        SecretKey claveAES = cifradorAES.generarClave();
        byte[] claveBytes = cifradorAES.claveABytes(claveAES);

        String salt = KeyGenerators.string().generateKey();
        BytesEncryptor encryptor = Encryptors.stronger(request.getContrasena(), salt);
        byte[] claveCifradaBytes = encryptor.encrypt(claveBytes);

        String claveCifradaBase64 = Base64.getEncoder().encodeToString(claveCifradaBytes);
        String claveFinal = salt + ":" + claveCifradaBase64;
        nuevo.setClaveCifDesPersonal(claveFinal);

        Usuario guardado = RepoUsuario.save(nuevo);
        String token = jwtTokenProvider.generarToken(guardado.getCorreo(), guardado.getNombre());

        byte[] claveAESDescifradaBytes = encryptor.decrypt(claveCifradaBytes);
        String claveAESDescifradaParaClienteBase64 = Base64.getEncoder().encodeToString(claveAESDescifradaBytes);

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setNombre(guardado.getNombre());
        response.setCorreo(guardado.getCorreo());
        response.setClaveCifDesPersonal(claveAESDescifradaParaClienteBase64);
        response.setIdUsuario(guardado.getIdUsuario());

        // Devolvemos los pines para que Android los guarde de una vez
        response.setPinReal(guardado.getPinRealHash());
        response.setPinSeguro(guardado.getPinSeguroHash());

        return response;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        Usuario usuario = RepoUsuario.findByCorreo(request.getCorreo())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        if (!passwordEncoder.matches(request.getContrasena(), usuario.getContrasena())) {
            throw new RuntimeException("Contraseña incorrecta.");
        }

        String clavePersonalCifradaAlmacenada = usuario.getClaveCifDesPersonal();
        if (clavePersonalCifradaAlmacenada == null || !clavePersonalCifradaAlmacenada.contains(":")) {
            throw new RuntimeException("Clave personal cifrada no disponible.");
        }

        String[] partes = clavePersonalCifradaAlmacenada.split(":");
        String saltParaDescifrado = partes[0];
        String claveAESCifradaSoloBase64 = partes[1];

        byte[] claveAESCifradaBytes = Base64.getDecoder().decode(claveAESCifradaSoloBase64);
        BytesEncryptor desencriptadorClaveAES = Encryptors.stronger(request.getContrasena(), saltParaDescifrado);
        byte[] claveAESDescifradaBytes = desencriptadorClaveAES.decrypt(claveAESCifradaBytes);
        String claveAESDescifradaParaClienteBase64 = Base64.getEncoder().encodeToString(claveAESDescifradaBytes);

        String token = jwtTokenProvider.generarToken(usuario.getCorreo(), usuario.getNombre());

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setNombre(usuario.getNombre());
        response.setCorreo(usuario.getCorreo());
        response.setIdUsuario(usuario.getIdUsuario());
        response.setClaveCifDesPersonal(claveAESDescifradaParaClienteBase64);

        // IMPORTANTE: Agregamos los pines a la respuesta del Login
        response.setPinReal(usuario.getPinRealHash());
        response.setPinSeguro(usuario.getPinSeguroHash());

        return response;
    }

    @Override
    public SecretKey recuperarClaveAES(Integer idUsuario, String contrasena) {
        Usuario usuario = RepoUsuario.findById(idUsuario).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        String[] partes = usuario.getClaveCifDesPersonal().split(":");
        BytesEncryptor decryptor = Encryptors.stronger(contrasena, partes[0]);
        try {
            byte[] claveBytes = decryptor.decrypt(Base64.getDecoder().decode(partes[1]));
            return new SecretKeySpec(claveBytes, "AES");
        } catch (Exception e) {
            throw new RuntimeException("No se pudo descifrar la clave AES.");
        }
    }

    @Override
    public Optional<Usuario> obtenerPorCorreo(String correo) {
        return RepoUsuario.findByCorreo(correo);
    }

    @Override
    public void cambiarContrasena(String correo, String contrasenaActual, String nuevaContrasena) {
        Usuario usuario = RepoUsuario.findByCorreo(correo).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(contrasenaActual, usuario.getContrasena())) {
            throw new RuntimeException("Contraseña actual incorrecta");
        }

        String claveCifradaBase64 = usuario.getClaveCifDesPersonal();
        String[] partes = claveCifradaBase64.split(":");
        BytesEncryptor decryptor = Encryptors.stronger(contrasenaActual, partes[0]);
        byte[] claveAESBytes = decryptor.decrypt(Base64.getDecoder().decode(partes[1]));

        String nuevoSalt = KeyGenerators.string().generateKey();
        BytesEncryptor encryptor = Encryptors.stronger(nuevaContrasena, nuevoSalt);
        usuario.setClaveCifDesPersonal(nuevoSalt + ":" + Base64.getEncoder().encodeToString(encryptor.encrypt(claveAESBytes)));
        usuario.setContrasena(passwordEncoder.encode(nuevaContrasena));

        RepoUsuario.save(usuario);
    }

    @Override
    public void recuperarContrasenaSinToken(String correo, String contrasenaActual, String nuevaContrasena) {
        cambiarContrasena(correo, contrasenaActual, nuevaContrasena);
    }

    @Override
    public Boolean isBiometricoActivo(String correo) {
        return RepoUsuario.findByCorreo(correo)
                .map(Usuario::getHuella)
                .orElse(false);
    }

    @Override
    public LoginResponse loginBiometrico(String correo) {
        Usuario usuario = RepoUsuario.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        String token = jwtTokenProvider.generarToken(usuario.getCorreo(), usuario.getNombre());

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setCorreo(usuario.getCorreo());
        response.setNombre(usuario.getNombre());
        response.setIdUsuario(usuario.getIdUsuario());
        response.setClaveCifDesPersonal(usuario.getClaveCifDesPersonal());

        // También enviamos pines en login biométrico
        response.setPinReal(usuario.getPinRealHash());
        response.setPinSeguro(usuario.getPinSeguroHash());

        return response;
    }
}

