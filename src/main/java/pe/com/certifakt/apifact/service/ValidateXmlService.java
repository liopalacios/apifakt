package pe.com.certifakt.apifact.service;

import pe.com.certifakt.apifact.exception.ServiceException;

public interface ValidateXmlService {

    void validate(String xmlBase64, String nameDocument, String ublVersion) throws ServiceException;

}
