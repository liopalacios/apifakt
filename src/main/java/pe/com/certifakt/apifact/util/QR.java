/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.com.certifakt.apifact.util;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;
import pe.com.certifakt.apifact.exception.QRGenerationException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;


public class QR {
    public static File generateQR(File file, String text, int h, int w) throws QRGenerationException {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(text, com.google.zxing.BarcodeFormat.QR_CODE, w, h);
            BufferedImage image = new BufferedImage(matrix.getWidth(), matrix.getHeight(), BufferedImage.TYPE_INT_RGB);
            image.createGraphics();
            Graphics2D graphics = (Graphics2D) image.getGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, matrix.getWidth(), matrix.getHeight());
            graphics.setColor(Color.BLACK);
            for (int i = 0; i < matrix.getWidth(); i++) {
                for (int j = 0; j < matrix.getHeight(); j++) {
                    if (matrix.get(i, j)) {
                        graphics.fillRect(i, j, 1, 1);
                    }
                }
            }
            ImageIO.write(image, "png", file);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new QRGenerationException(ex.getMessage());
        }
        return file;
    }

    public static String decoder(File file) throws Exception {
        FileInputStream inputStream = new FileInputStream(file);
        BufferedImage image = ImageIO.read(inputStream);
        LuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        Result result = reader.decode(bitmap);
        return result.getText();
    }
}
