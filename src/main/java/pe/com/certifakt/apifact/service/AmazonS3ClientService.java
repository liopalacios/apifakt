package pe.com.certifakt.apifact.service;

import org.springframework.web.multipart.MultipartFile;
import pe.com.certifakt.apifact.dto.inter.RegisterFileUploadInterDto;
import pe.com.certifakt.apifact.enums.TipoArchivoEnum;
import pe.com.certifakt.apifact.model.CompanyEntity;
import pe.com.certifakt.apifact.model.RegisterDownloadUploadEntity;
import pe.com.certifakt.apifact.model.RegisterFileUploadEntity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Map;

public interface AmazonS3ClientService {

    //void uploadFile(MultipartFile multipartFile, boolean enablePublicReadAccess);

    RegisterFileUploadEntity uploadFileStorage(MultipartFile multipartFile, String folder);

   // RegisterFileUploadEntity uploadFileStorage(byte[] filebytes, String nameFile, String folder);

    //RegisterFileUploadEntity uploadFileStorage(MultipartFile multipartFile, String folder, Integer idEmpresa);

   // RegisterFileUploadEntity uploadFileStorage(File file, String nameFile, String folder, CompanyEntity company);

    RegisterFileUploadEntity uploadFileStorage(InputStream inputStream, String nameFile, String folder, CompanyEntity company);
    /*********************************************************************************************************************************/
    RegisterDownloadUploadEntity uploadDocumentStorage(InputStream inputStream, String nameFile, String folder, CompanyEntity company);
    /*********************************************************************************************************************************/


    Map<String, String> uploadFilePublic(MultipartFile multipartFile, Integer idEmpresa);

   // ByteArrayOutputStream downloadFile(String keyName);

    ByteArrayInputStream downloadFileExcel(Long id, TipoArchivoEnum tipoArchivoEnum);

    ByteArrayInputStream downloadFileInvoice(Long id, String uuid, TipoArchivoEnum tipoArchivoEnum);
    ByteArrayInputStream downloadFileOther(Long id, String uuid, TipoArchivoEnum tipoArchivoEnum);
    ByteArrayInputStream downloadFileGuia(Long id, String uuid, TipoArchivoEnum tipoArchivoEnum);

    ByteArrayOutputStream downloadFile(String keyName, String folder);

    ByteArrayOutputStream downloadFileStorage(Long id);

    ByteArrayInputStream downloadFileStorage(RegisterFileUploadInterDto fileStorage);

    ByteArrayInputStream downloadFileStorage(RegisterFileUploadEntity fileStorage);

    ByteArrayInputStream downloadFileStorage(RegisterDownloadUploadEntity fileStorage);

    String downloadFileStorageInB64(RegisterFileUploadInterDto fileStorage);

    InputStream downloadFileInvoiceName(TipoArchivoEnum cdr, String namedoc);

    //byte[] downloadFileStorageInBytes(RegisterFileUploadEntity fileStorage);

    //void deleteFile(String fileName);

}
