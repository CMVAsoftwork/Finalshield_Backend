package com.finalshield.Services.Impl;

import com.finalshield.Services.CifradorAESService;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class CifradorAESServiceImpl implements CifradorAESService {
    // Firma para identificar los archivos cifrados.
    private static final String MAGIC_HEADER = "FSHIELD";

    // Algoritmo y modo de cifrado.
    private static final String ALGORITMO = "AES";
    private static final String ALGORITMO_COMPLETO = "AES/CBC/PKCS5Padding";

    // Genera clave aleatoria para el cifrado.
    private IvParameterSpec generarIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    // Genera clave de 256 bits
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

    // Convierte una clave en un array de bytes.
    @Override
    public byte[] claveABytes(SecretKey clave) {
        return clave.getEncoded();
    }

    // Convierte un array de bytes en una clave AES.
    @Override
    public SecretKey bytesAClave(byte[] bytes) {
        return new SecretKeySpec(bytes, ALGORITMO);
    }

    // Convierte una clave AES a una cadena de texto base64.
    public String claveABase64(SecretKey clave) {
        return Base64.getEncoder().encodeToString(clave.getEncoded());
    }

    // Convierte una cadena de texto base64 en una clave AES.
    public SecretKey base64AClave(String base64) {
        try {
            // 1. Limpieza de caracteres no permitidos
            String cleanedBase64 = base64.replaceAll("[^A-Za-z0-9+/=]", "");
            byte[] bytesOriginales = Base64.getDecoder().decode(cleanedBase64);

            // 2. Si la longitud no es 16, 24 o 32, forzamos una longitud válida usando SHA-256
            if (bytesOriginales.length != 16 && bytesOriginales.length != 24 && bytesOriginales.length != 32) {
                MessageDigest sha = MessageDigest.getInstance("SHA-256");
                byte[] keyBytes = sha.digest(bytesOriginales); // Esto siempre devuelve 32 bytes (256 bits)
                return new SecretKeySpec(keyBytes, ALGORITMO);
            }

            return new SecretKeySpec(bytesOriginales, ALGORITMO);
        } catch (Exception e) {
            throw new RuntimeException("Error procesando la llave de cifrado: " + e.getMessage());
        }
    }

    // Cifra un archivo físico y lo guarda en otro archivo, con header y IV al inicio del archivo.
    @Override
    public void cifrarArchivo(File archivoOriginal, File archivoDestino, SecretKey clave)
            throws IOException, GeneralSecurityException {
        int intentosMaximos = 3;
        int intentoActual = 0;
        boolean exito = false;

        // Reintento
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

                    // Escribe header y IV al inicio del archivo.
                    fos.write(MAGIC_HEADER.getBytes(StandardCharsets.UTF_8));
                    fos.write(ivSpec.getIV());

                    // Cifra el archivo original y escribirlo en el archivo cifrado.
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

    // Cifra un archivo adjunto en formato base64 con IV al inicio en stream
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

    // Cifra un arreglo de bytes en formato base64 con IV al inicio.
    public byte[] cifrarBytes(byte[] datosOriginales, SecretKey clave) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITMO_COMPLETO);
        IvParameterSpec ivSpec = generarIv();
        cipher.init(Cipher.ENCRYPT_MODE, clave, ivSpec);

        byte[] cifradoConIV = new byte[ivSpec.getIV().length + cipher.getOutputSize(datosOriginales.length)];
        System.arraycopy(ivSpec.getIV(), 0, cifradoConIV, 0, ivSpec.getIV().length);
        byte[] datosCifrados = cipher.doFinal(datosOriginales);
        System.arraycopy(datosCifrados, 0, cifradoConIV, ivSpec.getIV().length, datosCifrados.length);
        return cifradoConIV;
    }

    // Cifra datos en formato base64 con IV al inicio.
    public String cifrarTexto(String textoPlano, SecretKey clave) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITMO_COMPLETO);
            IvParameterSpec ivSpec = generarIv();
            cipher.init(Cipher.ENCRYPT_MODE, clave, ivSpec);

            byte[] cifrado = cipher.doFinal(textoPlano.getBytes(StandardCharsets.UTF_8));
            byte[] combinado = new byte[ivSpec.getIV().length + cifrado.length];
            System.arraycopy(ivSpec.getIV(), 0, combinado, 0, ivSpec.getIV().length);
            System.arraycopy(cifrado, 0, combinado, ivSpec.getIV().length, cifrado.length);
            return Base64.getEncoder().encodeToString(combinado);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Error al cifrar el texto", e);
        }
    }

    // Descifra un archivo adjunto cifrado con header y IV al inicio del archivo
    @Override
    public void descifrarArchivo(File archivoCifrado, File archivoDescifrado, SecretKey clave) throws IOException, GeneralSecurityException {
        try (FileInputStream fis = new FileInputStream(archivoCifrado);
             FileOutputStream fos = new FileOutputStream(archivoDescifrado)) {

            // Lee header y IV del archivo cifrado.
            byte[] header = new byte[MAGIC_HEADER.length()];
            if (fis.read(header) != MAGIC_HEADER.length()) {
                throw new IOException("No se pudo leer el encabezado del archivo");
            }

            String firma = new String(header, StandardCharsets.UTF_8);
            if (!MAGIC_HEADER.equals(firma)) {
                throw new SecurityException("El archivo no contiene la firma esperada. ¿Ya está descifrado?");
            }

            // Extrae IV del archivo cifrado.
            byte[] iv = new byte[16];
            if (fis.read(iv) != 16) {
                throw new IOException("No se pudo leer el IV del archivo cifrado.");
            }
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance(ALGORITMO_COMPLETO);
            cipher.init(Cipher.DECRYPT_MODE, clave, ivSpec);

            // Descifra archivo cifrado
            try (CipherInputStream cis = new CipherInputStream(fis, cipher)) {
                byte[] buffer = new byte[1024];
                int leido;
                while ((leido = cis.read(buffer)) != -1) {
                    fos.write(buffer, 0, leido);
                }
            }
        }
    }

    // Descifra archivos cifrados con IV en formato base64 en un stream.
    public void descifrarArchivoStream(InputStream inputStream, OutputStream outputStream, SecretKey clave) throws Exception {
        byte[] header = new byte[MAGIC_HEADER.length()];
        int bytesLeidosHeader = inputStream.read(header);

        if (bytesLeidosHeader != MAGIC_HEADER.length()) {
            throw new IOException("No se pudo leer el encabezado completo del stream.");
        }

        String firma = new String(header, StandardCharsets.UTF_8);
        if (!MAGIC_HEADER.equals(firma)) {
            throw new SecurityException("El stream no contiene la firma esperada ('" + MAGIC_HEADER + "').");
        }

        // Extrae IV del stream
        byte[] iv = new byte[16];
        if (inputStream.read(iv) != 16) {
            throw new IOException("No se pudo leer el IV del stream cifrado.");
        }
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance(ALGORITMO_COMPLETO);
        cipher.init(Cipher.DECRYPT_MODE, clave, ivSpec);

        // Descifra stream
        try (CipherInputStream cis = new CipherInputStream(inputStream, cipher)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = cis.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }

    // Descifra datos cifrados con IV en formato base64
    public byte[] descifrarBytes(byte[] datosCifradosConIV, SecretKey clave) throws Exception {
        byte[] iv = new byte[16];
        System.arraycopy(datosCifradosConIV, 0, iv, 0, iv.length);
        byte[] datosCifrados = new byte[datosCifradosConIV.length - iv.length];
        System.arraycopy(datosCifradosConIV, iv.length, datosCifrados, 0, datosCifrados.length);

        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        Cipher cipher = Cipher.getInstance(ALGORITMO_COMPLETO);
        cipher.init(Cipher.DECRYPT_MODE, clave, ivSpec);
        return cipher.doFinal(datosCifrados);
    }

    // Descifra texto cifrado con IV en formato base64
    public String descifrarTexto(String textoCifradoBase64ConIV, SecretKey clave) throws GeneralSecurityException {
        try {
            byte[] combinado = Base64.getDecoder().decode(textoCifradoBase64ConIV);
            byte[] iv = new byte[16];
            System.arraycopy(combinado, 0, iv, 0, iv.length);
            byte[] cifrado = new byte[combinado.length - iv.length];
            System.arraycopy(combinado, iv.length, cifrado, 0, cifrado.length);

            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance(ALGORITMO_COMPLETO);
            cipher.init(Cipher.DECRYPT_MODE, clave, ivSpec);
            byte[] descifrado = cipher.doFinal(cifrado);
            return new String(descifrado, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new GeneralSecurityException("Error al descifrar el texto: " + e.getMessage(), e);
        }
    }

    // Cifra un archivo adjunto con header y IV al inicio del archivo
    public byte[] descifrarAdjuntoConHeader(byte[] datosAdjunto, SecretKey clave) throws Exception {
        int IV_SIZE = 16;
        int HEADER_SIZE = MAGIC_HEADER.length();
        if (datosAdjunto.length < HEADER_SIZE + IV_SIZE) {
            throw new IllegalArgumentException("El contenido adjunto es demasiado corto para descifrar.");
        }
        byte[] iv = new byte[IV_SIZE];
        System.arraycopy(datosAdjunto, HEADER_SIZE, iv, 0, IV_SIZE);

        int datosCifradosOffset = HEADER_SIZE + IV_SIZE;
        byte[] datosCifrados = new byte[datosAdjunto.length - datosCifradosOffset];
        System.arraycopy(datosAdjunto, datosCifradosOffset, datosCifrados, 0, datosCifrados.length);

        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance(ALGORITMO_COMPLETO);
        cipher.init(Cipher.DECRYPT_MODE, clave, ivSpec);

        return cipher.doFinal(datosCifrados);
    }
}
