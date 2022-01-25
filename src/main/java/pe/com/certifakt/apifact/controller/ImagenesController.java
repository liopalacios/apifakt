package pe.com.certifakt.apifact.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pe.com.certifakt.apifact.exception.ServiceException;
import pe.com.certifakt.apifact.model.RegisterFileUploadEntity;
import pe.com.certifakt.apifact.service.AmazonS3ClientService;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;

@Controller
@AllArgsConstructor
@Slf4j
@RequestMapping("/api")
public class ImagenesController {


    private final AmazonS3ClientService amazonS3ClientService;


    @RequestMapping(path = "/imagenes/{idFile}", method = RequestMethod.GET)
    public void descargaimagen(HttpServletResponse response, @PathVariable Long idFile) throws IOException {

        ByteArrayOutputStream bos = amazonS3ClientService.downloadFileStorage(idFile);
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        IOUtils.copy(bis, response.getOutputStream());
    }


    @PostMapping("/imagenes/subirimagen")
    public @ResponseBody String uploadImage(Principal principal, @RequestParam("imagen") MultipartFile imagen, HttpServletResponse response) {

        if (principal != null)
            log.info(principal.toString());

        RegisterFileUploadEntity resp;
        try {
            resp = amazonS3ClientService.uploadFileStorage(imagen, "imagenes");
        } catch (Exception e) {
            e.printStackTrace();

            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF-8");
            throw new ServiceException("No se pudo subir la imagen");
        }

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        return resp.getIdRegisterFileSend().toString();
    }

}
