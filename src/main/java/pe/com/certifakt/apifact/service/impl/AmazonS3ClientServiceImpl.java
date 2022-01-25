package pe.com.certifakt.apifact.service.impl;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import pe.com.certifakt.apifact.dto.inter.PaymentVoucherInterDto;
import pe.com.certifakt.apifact.dto.inter.PaymentVoucherInterFileDto;
import pe.com.certifakt.apifact.dto.inter.RegisterFileUploadInterDto;
import pe.com.certifakt.apifact.enums.TipoArchivoEnum;
import pe.com.certifakt.apifact.exception.ServiceException;
import pe.com.certifakt.apifact.model.*;
import pe.com.certifakt.apifact.repository.*;
import pe.com.certifakt.apifact.service.AmazonS3ClientService;
import pe.com.certifakt.apifact.service.ExcelService;
import pe.com.certifakt.apifact.util.UtilArchivo;
import pe.com.certifakt.apifact.util.UtilDate;

import java.io.*;
import java.util.*;

@Component
@Slf4j
public class AmazonS3ClientServiceImpl implements AmazonS3ClientService {

    @Autowired
    private AmazonS3 s3client;

    @Value("${apifact.aws.s3.bucket}")
    private String bucketName;

    @Value("${apifact.aws.s3.baseUrl}")
    private String baseUrl;

    @Autowired
    ExcelService excelService;

    @Autowired
    private DowloadExcelRepository dowloadExcelRepository;
    @Autowired
    private RegisterFileUploadRepository fileStorageRepository;
    @Autowired
    private RegisterDownloadUploadRepository downloadUploadRepository;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private PaymentVoucherRepository paymentVoucherRepository;
    @Autowired
    private OtherCpeRepository otherCpeRepository;
    @Autowired
    private GuiaRemisionRepository guiaRemisionRepository;

    /*  @Async
      public void uploadFile(MultipartFile multipartFile, boolean enablePublicReadAccess) {
          String fileName = multipartFile.getOriginalFilename();

          try {
              //creating the file in the server (temporarily)
              File file = new File(fileName);
              FileOutputStream fos = new FileOutputStream(file);
              fos.write(multipartFile.getBytes());
              fos.close();

              PutObjectRequest putObjectRequest = new PutObjectRequest(this.bucketName, fileName, file);

              if (enablePublicReadAccess) {
                  putObjectRequest.withCannedAcl(CannedAccessControlList.PublicRead);
              }
              this.s3client.putObject(putObjectRequest);
              //removing the file created in the server
              file.delete();
          } catch (IOException | AmazonServiceException ex) {
              log.error("error [" + ex.getMessage() + "] occurred while uploading [" + fileName + "] ");
          }
      }
  */
    @Override
    public RegisterFileUploadEntity uploadFileStorage(MultipartFile multipartFile, String folder) {

        String fileName = multipartFile.getOriginalFilename();
        String fileNameKey = String.format("%s-%s", UUID.randomUUID(), fileName);
        String bucket = String.format("%s/%s", this.bucketName, folder);

        try {
            //creating the file in the server (temporarily)
            File file = new File(fileName);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(multipartFile.getBytes());
            fos.close();

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, fileNameKey, file);

            this.s3client.putObject(putObjectRequest);
            //removing the file created in the server
            file.delete();

            return fileStorageRepository.save(RegisterFileUploadEntity.builder()
                    .bucket(bucket)
                    .nombreGenerado(fileNameKey)
                    .nombreOriginal(fileName)
                    .build());


        } catch (IOException | AmazonServiceException ex) {
            log.error("error [" + ex.getMessage() + "] occurred while uploading [" + fileName + "] ");
            throw new ServiceException("Ocurrio un error al subir el archivo");
        }
    }

   /* @Override
    public RegisterFileUploadEntity uploadFileStorage(byte[] filebytes, String fileName, String folder) {
        String fileNameKey = String.format("%s-%s", UUID.randomUUID(), fileName);
        String bucket = String.format("%s/%s", this.bucketName, folder);

        try {
            //creating the file in the server (temporarily)
            File file = new File(fileName);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(filebytes);
            fos.close();

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, fileNameKey, file);

            this.s3client.putObject(putObjectRequest);
            //removing the file created in the server
            file.delete();

            return fileStorageRepository.save(RegisterFileUploadEntity.builder()
                    .bucket(bucket)
                    .nombreGenerado(fileNameKey)
                    .nombreOriginal(fileName)
                    .build());


        } catch (IOException | AmazonServiceException ex) {
            log.error("error [" + ex.getMessage() + "] occurred while uploading [" + fileName + "] ");
            throw new ServiceException("Ocurrio un error al subir el archivo");
        }
    }
*/

    /* @Override
     public RegisterFileUploadEntity uploadFileStorage(MultipartFile multipartFile, String folder, Integer idEmpresa) {

         Optional<CompanyEntity> company = companyRepository.findById(idEmpresa);
         if (!company.isPresent()) throw new ServiceException("Empresa no registrada");

         String fileName = multipartFile.getOriginalFilename();
         String fileNameKey = String.format("%s-%s", UUID.randomUUID(), fileName);
         String bucket = String.format("%s/%s/%s", this.bucketName, company.get().getRuc(), folder);

         try {
             //creating the file in the server (temporarily)
             File file = new File(fileName);
             FileOutputStream fos = new FileOutputStream(file);
             fos.write(multipartFile.getBytes());
             fos.close();

             PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, fileNameKey, file);

             this.s3client.putObject(putObjectRequest);
             //removing the file created in the server
             file.delete();

             return fileStorageRepository.save(RegisterFileUploadEntity.builder()
                     .bucket(bucket)
                     .nombreGenerado(fileNameKey)
                     .nombreOriginal(fileName)
                     .company(company.get())
                     .build());


         } catch (IOException | AmazonServiceException ex) {
             log.error("error [" + ex.getMessage() + "] occurred while uploading [" + fileName + "] ");
             throw new ServiceException("Ocurrio un error al subir el archivo");
         }
     }
 */
 /*   @Override
    public RegisterFileUploadEntity uploadFileStorage(File file, String nameFile, String folder, CompanyEntity company) {


        String fileNameKey = String.format("%s-%s", UUID.randomUUID(), nameFile);
        String bucket = String.format("%s/%s/%s", this.bucketName, company.getRuc(), folder);

        try {


            StopWatch watch = new StopWatch();
            watch.start();

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, fileNameKey, file);

            this.s3client.putObject(putObjectRequest);
            file.delete();

            RegisterFileUploadEntity resp = fileStorageRepository.save(RegisterFileUploadEntity.builder()
                    .bucket(bucket)
                    .nombreGenerado(fileNameKey)
                    .nombreOriginal(nameFile)
                    .company(company)
                    .build());

            watch.stop();
            log.info(String.format("%s %s %s", "Tiempo de Subida de archivo:", nameFile, watch.getTime()));

            return resp;


        } catch (AmazonServiceException ex) {
            ex.printStackTrace();
            log.error("error [" + ex.getMessage() + "] occurred while uploading [" + nameFile + "] ");
            throw new ServiceException("Ocurrio un error al subir el archivo");
        }
    }
*/
    @Override
    public RegisterFileUploadEntity uploadFileStorage(InputStream inputStream, String nameFile, String folder, CompanyEntity company) {


        String periodo = UtilDate.dateNowToString("MMyyyy ");

        String fileNameKey = String.format("%s-%s", UUID.randomUUID(), nameFile);
        String bucket = String.format("%s/archivos/%s/%s/%s", this.bucketName, company.getRuc(), folder, periodo);

        try {


            StopWatch watch = new StopWatch();
            watch.start();

            ObjectMetadata metadata = new ObjectMetadata();
            byte[] resultByte = DigestUtils.md5(inputStream);
            inputStream.reset();
            byte[] contentBytes = IOUtils.toByteArray(inputStream);
            String streamMD5 = new String(Base64.encodeBase64(resultByte));
            Long contentLength = Long.valueOf(contentBytes.length);
            metadata.setContentMD5(streamMD5);
            metadata.setContentLength(contentLength);

            inputStream.reset();

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, fileNameKey, inputStream, metadata);

            this.s3client.putObject(putObjectRequest);

            RegisterFileUploadEntity resp = fileStorageRepository.save(RegisterFileUploadEntity.builder()
                    .bucket(bucket)
                    .nombreGenerado(fileNameKey)
                    .nombreOriginal(nameFile)
                    .company(company)
                    .build());

            watch.stop();
            log.info(String.format("%s %s %s", "Tiempo de Subida de archivo:", nameFile, watch.getTime()));

            return resp;


        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("error [" + ex.getMessage() + "] occurred while uploading [" + nameFile + "] ");
            throw new ServiceException("Ocurrio un error al subir el archivo: " + ex.getMessage());
        }
    }
    /***************************************************************************************************************************************/

    @Override
    public RegisterDownloadUploadEntity uploadDocumentStorage(InputStream inputStream, String nameFile, String folder, CompanyEntity company) {

        String fileNameKey = String.format("%s-%s", UUID.randomUUID(), nameFile);
        String bucket = String.format("%s/%s", this.bucketName, folder);

        try {


            StopWatch watch = new StopWatch();
            watch.start();

            ObjectMetadata metadata = new ObjectMetadata();
            byte[] resultByte = DigestUtils.md5(inputStream);
            inputStream.reset();
            byte[] contentBytes = IOUtils.toByteArray(inputStream);
            String streamMD5 = new String(Base64.encodeBase64(resultByte));
            Long contentLength = Long.valueOf(contentBytes.length);
            metadata.setContentMD5(streamMD5);
            metadata.setContentLength(contentLength);

            inputStream.reset();

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, fileNameKey, inputStream, metadata);


            this.s3client.putObject(putObjectRequest);

            RegisterDownloadUploadEntity resp = downloadUploadRepository.save(RegisterDownloadUploadEntity.builder()
                    .bucket(bucket)
                    .nombreGenerado(fileNameKey)
                    .nombreOriginal(nameFile)
                    .company(company)
                    .build());

            watch.stop();
            log.info(String.format("%s %s %s", "Tiempo de Subida de archivo:", nameFile, watch.getTime()));

            return resp;


        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("error [" + ex.getMessage() + "] occurred while uploading [" + nameFile + "] ");
            throw new ServiceException("Ocurrio un error al subir el archivo: " + ex.getMessage());
        }
    }
    /*
    @Autowired
    public RegisterFileUploadEntity uploadFileStorage(InputStream inputStream, String folder) {

        String fileName = inputStream.toString();
        String fileNameKey = String.format("%s-%s", UUID.randomUUID(), fileName);
        String bucket = String.format("%s/%s", this.bucketName, folder);

        try {
            //creating the file in the server (temporarily)
            File file = new File(fileName);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(inputStream.read());
            fos.close();

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, fileNameKey, file);

            this.s3client.putObject(putObjectRequest);
            //removing the file created in the server
            file.delete();

            return fileStorageRepository.save(RegisterFileUploadEntity.builder()
                    .bucket(bucket)
                    .nombreGenerado(fileNameKey)
                    .nombreOriginal(fileName)
                    .build());


        } catch (IOException | AmazonServiceException ex) {
            log.error("error [" + ex.getMessage() + "] occurred while uploading [" + fileName + "] ");
            throw new ServiceException("Ocurrio un error al subir el archivo");
        }
    }
    /***************************************************************************************************************************************/


    @Override
    public Map<String, String> uploadFilePublic(MultipartFile multipartFile, Integer idEmpresa) {
        Optional<CompanyEntity> company = companyRepository.findById(idEmpresa);
        if (!company.isPresent()) throw new ServiceException("Empresa no registrada");

        String fileName = multipartFile.getOriginalFilename();
        String fileNameKey = String.format("%s-%s", UUID.randomUUID(), fileName);
        String bucket = String.format("%s/%s/%s", this.bucketName, company.get().getRuc(), "public/images");

        try {
            //creating the file in the server (temporarily)
            File file = new File(fileName);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(multipartFile.getBytes());
            fos.close();

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, fileNameKey, file);
            putObjectRequest.withCannedAcl(CannedAccessControlList.PublicRead);

            this.s3client.putObject(putObjectRequest);
            //removing the file created in the server
            file.delete();

            Map<String, String> resp = new HashMap<>();
            resp.put("url", String.format("%s%s/%s", baseUrl, bucket, fileNameKey));
            return resp;


        } catch (IOException | AmazonServiceException ex) {
            log.error("error [" + ex.getMessage() + "] occurred while uploading [" + fileName + "] ");
            throw new ServiceException("Ocurrio un error al subir el archivo");
        }

    }

  /*  @Override
    public ByteArrayOutputStream downloadFile(String keyName) {
        return getFile(bucketName, keyName);
    }
*/
    @Override
    public ByteArrayInputStream downloadFileInvoice(Long id, String uuid, TipoArchivoEnum tipoArchivoEnum) {
        String tipo = tipoArchivoEnum.name();
        RegisterFileUploadInterDto registerFileUploadInterDto = paymentVoucherRepository.findByIdPaymentVoucherAndUuidTipo(id, uuid, tipo);
        /*RegisterFileUploadEntity file = new RegisterFileUploadEntity();
        RegisterFileUploadInterDto
        if (tipoArchivoEnum.equals(TipoArchivoEnum.XML)) {
            file = paymentVoucherEntity.getXmlActivo();
        } else {
            file = paymentVoucherEntity.getCdrActivo();
        }*/
        System.out.println("donwload file Invoice ");
        return downloadFileStorage(registerFileUploadInterDto);
    }
    @Override
    public InputStream downloadFileInvoiceName(TipoArchivoEnum tipoArchivoEnum, String namedoc) {
        String tipo = tipoArchivoEnum.name();
        PaymentVoucherInterDto dto = paymentVoucherRepository.findByIdentificadorDocumentoName(namedoc);
        RegisterFileUploadInterDto registerFileUploadInterDto = paymentVoucherRepository
                .findByIdPaymentVoucherAndUuidTipo(dto.getId(), dto.getUuid(), tipo);
        /*RegisterFileUploadEntity file = new RegisterFileUploadEntity();
        RegisterFileUploadInterDto
        if (tipoArchivoEnum.equals(TipoArchivoEnum.XML)) {
            file = paymentVoucherEntity.getXmlActivo();
        } else {
            file = paymentVoucherEntity.getCdrActivo();
        }*/
        System.out.println("donwload file Invoice ");
        return downloadFileStorage(registerFileUploadInterDto);
    }
    @Override
    public ByteArrayInputStream downloadFileExcel(Long id, TipoArchivoEnum tipoArchivoEnum) {
        DowloadExcelEntity dowloadExcelEntity = dowloadExcelRepository.findById(id).get();
        RegisterDownloadUploadEntity file = null;

        file = dowloadExcelEntity.getXlsActivo();
        return downloadFileStorage(file);
    }

    @Override
    public ByteArrayInputStream downloadFileOther(Long id, String uuid, TipoArchivoEnum tipoArchivoEnum) {
        //OtherCpeEntity cpe = otherCpeRepository.findByIdOtroCPEAndUuid(id, uuid);
        RegisterFileUploadInterDto file = otherCpeRepository.findByIdOtroCPEAndUuidTipo(id, uuid,tipoArchivoEnum.name());
        /*if (tipoArchivoEnum.equals(TipoArchivoEnum.XML)) {
            file = cpe.getXmlActivo();
        } else {
            file = cpe.getCdrActivo();
        }*/
        System.out.println("donwload file Otros ");
        System.out.println(file);
        return downloadFileStorage(file);
    }

    @Override
    public ByteArrayInputStream downloadFileGuia(Long id, String uuid, TipoArchivoEnum tipoArchivoEnum) {
        RegisterFileUploadInterDto file = guiaRemisionRepository.findByidGuiaRemisionAndUuidTipo(id, uuid,tipoArchivoEnum.name());
        return downloadFileStorage(file);
    }



    @Override
    public ByteArrayOutputStream downloadFileStorage(Long id) {
        Optional<RegisterFileUploadEntity> fileStorage = fileStorageRepository.findById(id);
        if (!fileStorage.isPresent()) throw new ServiceException("Archivo no encontrado");


        String bucket, name;

        if (fileStorage.get().getIsOld() == null || !fileStorage.get().getIsOld()) {
            bucket = fileStorage.get().getBucket();
            name = fileStorage.get().getNombreGenerado();
        } else {
            bucket = String.format("%s/imagenes", this.bucketName);
            name = String.format("%s.%s", fileStorage.get().getUuid(), fileStorage.get().getExtension());
        }

        return getFile(bucket, name);
    }

    @Override
    public ByteArrayInputStream downloadFileStorage(RegisterFileUploadInterDto fileStorage) {

        String bucket, name;

        if (fileStorage.getIsOld() == null || !fileStorage.getIsOld()) {
            bucket = fileStorage.getBucket();
            name = fileStorage.getNombreGenerado();
        } else {
            bucket = String.format("%s/archivos_old/%s", this.bucketName, fileStorage.getRucCompany());
            name = String.format("%s.%s", fileStorage.getUuid(), fileStorage.getExtension());
        }
        System.out.println("bucket: "+bucket);
        System.out.println("name: "+name);
        return new ByteArrayInputStream(getFile(bucket, name).toByteArray());
    }

    @Override
    public ByteArrayInputStream downloadFileStorage(RegisterFileUploadEntity fileStorage) {
        String bucket, name;

        if (fileStorage.getIsOld() == null || !fileStorage.getIsOld()) {
            bucket = fileStorage.getBucket();
            name = fileStorage.getNombreGenerado();
        } else {
            bucket = String.format("%s/archivos_old/%s", this.bucketName, fileStorage.getRucCompany());
            name = String.format("%s.%s", fileStorage.getUuid(), fileStorage.getExtension());
        }

        return new ByteArrayInputStream(getFile(bucket, name).toByteArray());
    }

    @Override
    public ByteArrayInputStream downloadFileStorage(RegisterDownloadUploadEntity fileStorage) {
        String bucket, name;

        if (fileStorage.getIsOld() == null || !fileStorage.getIsOld()) {
            bucket = fileStorage.getBucket();
            System.out.println("bucket Entity: "+bucket);
            name = fileStorage.getNombreGenerado();
            System.out.println("name: "+name);
        } else {
            bucket = String.format("%s/archivos_old/%s", this.bucketName, fileStorage.getRucCompany());
            name = String.format("%s.%s", fileStorage.getExtension());
        }

        return new ByteArrayInputStream(getFile(bucket, name).toByteArray());
    }

    @Override
    public String downloadFileStorageInB64(RegisterFileUploadInterDto fileStorage) {
        return UtilArchivo.binToB64(downloadFileStorage(fileStorage));
    }



    /* @Override
     public byte[] downloadFileStorageInBytes(RegisterFileUploadEntity fileStorage) {
         return getFile(fileStorage.getBucket(), fileStorage.getNombreGenerado()).toByteArray();
     }*/
   @Override
   public ByteArrayOutputStream downloadFile(String keyName, String folder) {
       return getFile(bucketName + "/" + folder, keyName);
   }

    public ByteArrayOutputStream getFile(String bucketName, String keyName) {
        System.out.println("INGRESO GETFILE");
        System.out.println(bucketName);
        System.out.println(keyName);
        try {

            StopWatch watch = new StopWatch();
            watch.start();

            S3Object s3object = this.s3client.getObject(new GetObjectRequest(bucketName, keyName));

            InputStream is = s3object.getObjectContent();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int len;
            byte[] buffer = new byte[4096];
            while ((len = is.read(buffer, 0, buffer.length)) != -1) {
                baos.write(buffer, 0, len);
            }

            watch.stop();
            log.info(String.format("%s %s %s", "Tiempo de Descarga de archivo:", keyName, watch.getTime()));

            return baos;
        } catch (Exception ioe) {
            log.info("NO SE ENCONTRO ARCHIVO EN S3 , BUCKET: "+bucketName + " NAME: "+keyName);
            log.error("Exception SERVICE : " + ioe.getMessage());
            throw new ServiceException("El servicio de storage está fuera de servicio, comuniquese con el administrador.");
        }
    }

  /*  @Async
    public void deleteFile(String fileName) {
        try {
            s3client.deleteObject(new DeleteObjectRequest(bucketName, fileName));
        } catch (AmazonServiceException ex) {
            log.error("error [" + ex.getMessage() + "] occurred while removing [" + fileName + "] ");
        }
    }*/
}
