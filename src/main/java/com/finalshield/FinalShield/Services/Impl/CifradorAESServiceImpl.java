package com.finalshield.FinalShield.Services.Impl;

import com.finalshield.FinalShield.Model.Archivo;
import com.finalshield.FinalShield.Repositorios.ArchivoRepositorio;
import com.finalshield.FinalShield.Services.CifradorAESService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

@Service
public class CifradorAESServiceImpl implements CifradorAESService {

    @Autowired
    private ArchivoRepositorio archivoRepositorio;

    private static final String MAGIC_HEADER = "FSHIELD";
    private static final String ALGORITMO = "AES";
    private static final String ALGORITMO_COMPLETO = "AES/CBC/PKCS5Padding";

    private IvParameterSpec generarIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    @Override
    public SecretKey generarClave() {
        try {
            KeyGenerator generador = KeyGenerator.getInstance(ALGORITMO);
            generador.init(256);
            return generador.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al generar clave AES", e);
        }
    }

    @Override
    public byte[] claveABytes(SecretKey clave) {
        return clave.getEncoded();
    }

    @Override
    public SecretKey bytesAClave(byte[] bytes) {
        return new SecretKeySpec(bytes, ALGORITMO);
    }

    @Override
    public String claveABase64(SecretKey clave) {
        return Base64.getEncoder().encodeToString(clave.getEncoded());
    }

    @Override
    public SecretKey base64AClave(String base64) {
        byte[] bytes = Base64.getDecoder().decode(base64);
        return new SecretKeySpec(bytes, ALGORITMO);
    }

    @Override
    public void cifrarArchivo(File archivoOriginal, File archivoDestino, SecretKey clave)
            throws IOException, GeneralSecurityException {
        int intentosMaximos = 3;
        int intentoActual = 0;
        boolean exito = false;

        while (!exito && intentoActual < intentosMaximos) {
            try {
                Thread.sleep(1000);

                if (!archivoOriginal.exists() || !archivoOriginal.canRead()) {
                    intentoActual++;
                    continue;
                }

                Cipher cipher = Cipher.getInstance(ALGORITMO_COMPLETO);
                IvParameterSpec ivSpec = generarIv();
                cipher.init(Cipher.ENCRYPT_MODE, clave, ivSpec);

                try (FileOutputStream fos = new FileOutputStream(archivoDestino)) {
                    fos.write(MAGIC_HEADER.getBytes(StandardCharsets.UTF_8));
                    fos.write(ivSpec.getIV());

                    try (FileInputStream fis = new FileInputStream(archivoOriginal);
                         CipherOutputStream cos = new CipherOutputStream(fos, cipher)) {

                        byte[] buffer = new byte[1024];
                        int leido;
                        while ((leido = fis.read(buffer)) != -1) {
                            cos.write(buffer, 0, leido);
                        }
                    }
                    exito = true;
                }
            } catch (IOException e) {
                if (intentoActual >= intentosMaximos - 1) {
                    throw new IOException("No se pudo acceder al archivo después de " +
                            intentosMaximos + " intentos: " + e.getMessage(), e);
                }
                intentoActual++;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("Operación interrumpida", e);
            }
        }
    }

    @Override
    public void descifrarArchivo(File archivoCifrado, File archivoDescifrado, SecretKey clave) throws IOException, GeneralSecurityException {
        try (FileInputStream fis = new FileInputStream(archivoCifrado)) {
            byte[] header = new byte[MAGIC_HEADER.length()];
            fis.read(header);
            if (!MAGIC_HEADER.equals(new String(header, StandardCharsets.UTF_8))) {
                throw new IllegalArgumentException("Header inválido, no es un archivo cifrado válido.");
            }

            byte[] ivBytes = new byte[16];
            fis.read(ivBytes);
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

            Cipher cipher = Cipher.getInstance(ALGORITMO_COMPLETO);
            cipher.init(Cipher.DECRYPT_MODE, clave, ivSpec);

            try (FileOutputStream fos = new FileOutputStream(archivoDescifrado);
                 CipherInputStream cis = new CipherInputStream(fis, cipher)) {
                byte[] buffer = new byte[1024];
                int leido;
                while ((leido = cis.read(buffer)) != -1) {
                    fos.write(buffer, 0, leido);
                }
            }
        }
    }

    @Override
    public String descifrarTexto(String textoCifradoBase64, SecretKey clave) throws GeneralSecurityException {
        byte[] datosCifrados = Base64.getDecoder().decode(textoCifradoBase64);
        Cipher cipher = Cipher.getInstance(ALGORITMO_COMPLETO);
        byte[] ivBytes = new byte[16];
        System.arraycopy(datosCifrados, 0, ivBytes, 0, 16);
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        cipher.init(Cipher.DECRYPT_MODE, clave, ivSpec);
        byte[] decrypted = cipher.doFinal(datosCifrados, 16, datosCifrados.length - 16);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    @Override
    public String cifrarTexto(String textoPlano, SecretKey clave) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITMO_COMPLETO);
            IvParameterSpec ivSpec = generarIv();
            cipher.init(Cipher.ENCRYPT_MODE, clave, ivSpec);
            byte[] encrypted = cipher.doFinal(textoPlano.getBytes(StandardCharsets.UTF_8));
            byte[] combined = new byte[ivSpec.getIV().length + encrypted.length];
            System.arraycopy(ivSpec.getIV(), 0, combined, 0, ivSpec.getIV().length);
            System.arraycopy(encrypted, 0, combined, ivSpec.getIV().length, encrypted.length);
            return Base64.getEncoder().encodeToString(combined);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void cifrarArchivoStream(File inputFile, OutputStream outputStream, SecretKey clave) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITMO_COMPLETO);
        IvParameterSpec ivSpec = generarIv();
        cipher.init(Cipher.ENCRYPT_MODE, clave, ivSpec);

        outputStream.write(MAGIC_HEADER.getBytes(StandardCharsets.UTF_8));
        outputStream.write(ivSpec.getIV());

        try (FileInputStream fis = new FileInputStream(inputFile);
             CipherOutputStream cos = new CipherOutputStream(outputStream, cipher)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                cos.write(buffer, 0, bytesRead);
            }
        }
    }

    @Override
    public byte[] descifrarBytes(byte[] datosCifradosConIV, SecretKey clave) throws Exception {
        byte[] ivBytes = new byte[16];
        System.arraycopy(datosCifradosConIV, 0, ivBytes, 0, 16);
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

        Cipher cipher = Cipher.getInstance(ALGORITMO_COMPLETO);
        cipher.init(Cipher.DECRYPT_MODE, clave, ivSpec);

        return cipher.doFinal(datosCifradosConIV, 16, datosCifradosConIV.length - 16);
    }

    @Override
    public void descifrarArchivoStream(InputStream inputStream, OutputStream outputStream, SecretKey clave) throws Exception {
        byte[] header = new byte[MAGIC_HEADER.length()];
        inputStream.read(header);
        if (!MAGIC_HEADER.equals(new String(header, StandardCharsets.UTF_8))) {
            throw new IllegalArgumentException("Header inválido");
        }

        byte[] ivBytes = new byte[16];
        inputStream.read(ivBytes);
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

        Cipher cipher = Cipher.getInstance(ALGORITMO_COMPLETO);
        cipher.init(Cipher.DECRYPT_MODE, clave, ivSpec);

        try (CipherInputStream cis = new CipherInputStream(inputStream, cipher)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = cis.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }

    @Override
    public byte[] cifrarBytes(byte[] datosOriginales, SecretKey clave) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITMO_COMPLETO);
        IvParameterSpec ivSpec = generarIv();
        cipher.init(Cipher.ENCRYPT_MODE, clave, ivSpec);
        byte[] encrypted = cipher.doFinal(datosOriginales);
        byte[] combined = new byte[MAGIC_HEADER.length() + ivSpec.getIV().length + encrypted.length];
        System.arraycopy(MAGIC_HEADER.getBytes(StandardCharsets.UTF_8), 0, combined, 0, MAGIC_HEADER.length());
        System.arraycopy(ivSpec.getIV(), 0, combined, MAGIC_HEADER.length(), ivSpec.getIV().length);
        System.arraycopy(encrypted, 0, combined, MAGIC_HEADER.length() + ivSpec.getIV().length, encrypted.length);
        return combined;
    }

    @Override
    public byte[] descifrarAdjuntoConHeader(byte[] datosAdjunto, SecretKey clave) throws Exception {
        byte[] header = new byte[MAGIC_HEADER.length()];
        System.arraycopy(datosAdjunto, 0, header, 0, MAGIC_HEADER.length());
        if (!MAGIC_HEADER.equals(new String(header, StandardCharsets.UTF_8))) {
            throw new IllegalArgumentException("Header inválido");
        }

        byte[] ivBytes = new byte[16];
        System.arraycopy(datosAdjunto, MAGIC_HEADER.length(), ivBytes, 0, 16);
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

        Cipher cipher = Cipher.getInstance(ALGORITMO_COMPLETO);
        cipher.init(Cipher.DECRYPT_MODE, clave, ivSpec);

        return cipher.doFinal(datosAdjunto, MAGIC_HEADER.length() + 16, datosAdjunto.length - MAGIC_HEADER.length() - 16);
    }

    @Override
    public byte[] obtenerContenidoCifrado(Integer idArchivo) {
        if (idArchivo == null) {
            System.err.println("ID de archivo es nulo.");
            return null;
        }

        Optional<Archivo> archivoOpt = archivoRepositorio.findById(idArchivo);

        if (archivoOpt.isEmpty()) {
            System.err.println("Archivo no encontrado en DB para ID: " + idArchivo);
            return null;
        }

        Archivo archivo = archivoOpt.get();
        String rutaFisica = archivo.getRutaArchivo();

        if (rutaFisica == null || rutaFisica.isEmpty()) {
            System.err.println("El campo rutaArchivo está vacío para ID: " + idArchivo);
            return null;
        }

        Path rutaCompleta = Paths.get(rutaFisica);
        File archivoCifrado = rutaCompleta.toFile();

        if (!archivoCifrado.exists() || !archivoCifrado.isFile()) {
            System.err.println("Archivo físico cifrado no encontrado en la ruta: " + rutaFisica);
            return null;
        }

        try {
            System.out.println("Leyendo archivo cifrado desde: " + rutaFisica);
            return Files.readAllBytes(rutaCompleta);
        } catch (IOException e) {
            System.err.println("Error al leer el archivo cifrado con ID: " + idArchivo + ". Error: " + e.getMessage());
            return null;
        }
    }
}
