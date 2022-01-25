/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.com.certifakt.apifact.util;

import org.apache.commons.io.FileUtils;
import pe.com.certifakt.apifact.exception.ServiceException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author Luis
 */
public class UtilArchivo {



    public static Boolean escribir(String ruta, String[] value) {
        FileWriter fichero = null;
        PrintWriter pw = null;
        try {
            fichero = new FileWriter(ruta);
            pw = new PrintWriter(fichero);
            for (int i = 0; i < value.length; i++) {
                pw.println(value[i]);
            }
            fichero.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Boolean escribir(String ruta, ByteArrayOutputStream value) {
        try {
            Path path = Paths.get(ruta);
            Files.write(path, value.toByteArray());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Boolean crearCarpeta(String ruta) {
        try {
            File directorio = new File(ruta);
            directorio.mkdir();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Boolean comprimir(String ruta, String extencion) {
        try {
            String sourceFile = ruta + "." + extencion;
            FileOutputStream fos = new FileOutputStream(ruta + ".zip");
            ZipOutputStream zipOut = new ZipOutputStream(fos);
            File fileToZip = new File(sourceFile);
            FileInputStream fis = new FileInputStream(fileToZip);
            ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
            zipOut.putNextEntry(zipEntry);
            final byte[] bytes = new byte[fis.available()];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
            zipOut.close();
            fis.close();
            fos.close();
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static File comprimir(ByteArrayOutputStream value, String extencionOrigen, String nombre) {
        try {
            File fileZipeado = File.createTempFile("temp", ".zip");
            FileOutputStream fos = new FileOutputStream(fileZipeado.getAbsoluteFile().toString());
            ZipOutputStream zipOut = new ZipOutputStream(fos);
            File fileToZip = new File(com.google.common.io.Files.createTempDir(), nombre + "." + extencionOrigen);
            Path path = Paths.get(fileToZip.getAbsoluteFile().toString());
            Files.write(path, value.toByteArray());
            FileInputStream fis = new FileInputStream(fileToZip);
            ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
            zipOut.putNextEntry(zipEntry);
            final byte[] bytes = new byte[fis.available()];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
            zipOut.close();
            fis.close();
            fos.close();
            return fileZipeado;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**********************************************************************************************************************************/



    /**********************************************************************************************************************************/


    public static File createFileTemp(String ruta, String[] value) {
        FileWriter fichero = null;
        PrintWriter pw = null;
        File file = null;
        try {
            file = new File(com.google.common.io.Files.createTempDir(), ruta);
            fichero = new FileWriter(file);
            pw = new PrintWriter(fichero);
            for (int i = 0; i < value.length; i++) {
                pw.println(value[i]);
            }
            fichero.close();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static File createFileTemp(String[] value, String extencion) {
        FileWriter fichero = null;
        PrintWriter pw = null;
        File file = null;
        try {
            file = File.createTempFile("temp", "." + extencion);
            fichero = new FileWriter(file);
            pw = new PrintWriter(fichero);
            for (int i = 0; i < value.length; i++) {
                pw.println(value[i]);
            }
            fichero.close();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static File createFileTemp(String extencion) {
        try {
            return File.createTempFile("temp", "." + extencion);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void copy(File source, File dest) {
        try {
            FileUtils.copyFile(source, dest);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

public static String generarCodigoHash(String nombreArchivo) {
		
		String codigoHash = null;

		try {			

			codigoHash=nombreArchivo.substring(nombreArchivo.indexOf("<ds:DigestValue>")+16, nombreArchivo.lastIndexOf("</ds:DigestValue>"));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return codigoHash;
	}


    public static ByteArrayInputStream b64ToByteArrayInputStream(String encodedString) {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] decodedByteArray = decoder.decode(encodedString);
        try {
             return new ByteArrayInputStream(decodedByteArray);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("Error en el formato del archivo xml");
        }
    }


    public static String binToB64(ByteArrayInputStream in){
        int n = in.available();
        byte[] bytes = new byte[n];
        try {
            in.read(bytes);
            Base64.Encoder encoder = Base64.getEncoder();
            return encoder.encodeToString(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

}
