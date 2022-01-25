/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.com.certifakt.apifact.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Luis
 */
public class UtilConversion {

    public static String decode(String s) {
        return StringUtils.newStringUtf8(Base64.decodeBase64(s));
    }

    public static String encodeFileToBase64(String s) {
        return Base64.encodeBase64String(StringUtils.getBytesUtf8(s));
    }

    public static String encodeFileToBase64(File file) throws IOException {
        byte bytes[] = loadFile(file);
        byte encoded[] = java.util.Base64.getEncoder().encode(bytes);
        return new String(encoded);
    }




    public static File decodeBase64ToFile(String s, String extencion) throws IOException {
        File fileTemp = UtilArchivo.createFileTemp(extencion);
        FileUtils.writeByteArrayToFile(fileTemp, java.util.Base64.getDecoder().decode(s));
        return fileTemp;
    }
    
    @SuppressWarnings("resource")
	private static byte[] loadFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        long length = file.length();
        if (length <= 0x7fffffffL);
        byte bytes[] = new byte[(int) length];
        int offset = 0;
        for (int numRead = 0; offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0; offset += numRead);
        if (offset < bytes.length) {
            throw new IOException(new StringBuilder().append("Could not completely read file ").append(file.getName()).toString());
        } else {
            is.close();
            return bytes;
        }
    }


}
