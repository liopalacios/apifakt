package pe.com.certifakt.apifact.util;

import lombok.extern.slf4j.Slf4j;
import pe.com.certifakt.apifact.bean.Log;
import pe.com.certifakt.apifact.enums.OperacionLogEnum;
import pe.com.certifakt.apifact.enums.SubOperacionLogEnum;
import pe.com.certifakt.apifact.enums.TipoLogEnum;

@Slf4j
public class Logger {

    public static void register(TipoLogEnum levelLog, String rucEmisor, String identificadorDocumento, OperacionLogEnum operacionEnum,
                                SubOperacionLogEnum subOperacionEnum, String mensaje) {

        StackTraceElement stackTraceCurrent = Thread.currentThread().getStackTrace()[2];
        String claseNameComplete = stackTraceCurrent.getClassName();

        Log logger = new Log();
        logger.setTipoLog(levelLog.getLevel());
        logger.setRucEmisor(rucEmisor);
        logger.setOperacion(operacionEnum.getOperacion());
        logger.setSubOperacion(subOperacionEnum.getSubOperacion());
        logger.setNombreClase(claseNameComplete.substring(claseNameComplete.lastIndexOf(".") + 1));
        logger.setNombreMetodo(stackTraceCurrent.getMethodName());
        logger.setIdentificadorDocumento(identificadorDocumento);
        logger.setMensaje(mensaje);

        printMessage(logger, levelLog);
    }

    public static void register(TipoLogEnum levelLog, String rucEmisor, String identificadorDocumento, OperacionLogEnum operacionEnum,
                                SubOperacionLogEnum subOperacionEnum, String mensaje, String parametros, Throwable excepcion) {

        StackTraceElement stackTraceCurrent = Thread.currentThread().getStackTrace()[2];
        String claseNameComplete = stackTraceCurrent.getClassName();

        Log logger = new Log();
        logger.setTipoLog(levelLog.getLevel());
        logger.setRucEmisor(rucEmisor);
        logger.setOperacion(operacionEnum.getOperacion());
        logger.setSubOperacion(subOperacionEnum.getSubOperacion());
        logger.setNombreClase(claseNameComplete.substring(claseNameComplete.lastIndexOf(".") + 1));
        logger.setNombreMetodo(stackTraceCurrent.getMethodName());
        logger.setIdentificadorDocumento(identificadorDocumento);
        logger.setParametros(parametros);
        logger.setMensaje(mensaje);
        logger.setExcepcion(excepcion);

        printMessage(logger, levelLog);
    }
    /****************************************************************************************************************************************/

    public static void register(TipoLogEnum info, String ruc, String nombreDocumento, OperacionLogEnum sendS3Excel, SubOperacionLogEnum sendS3) {
    }
/****************************************************************************************************************************************/
    private static void printMessage(Log logger, TipoLogEnum levelLog) {

        StringBuilder msgShowLog = new StringBuilder("[").append(logger.getTipoLog()).append("]");
        if (logger != null) {
            msgShowLog.append("[").append(logger.getRucEmisor()).append("]");
        }
        if (logger.getIdentificadorDocumento() != null) {
            msgShowLog.append("[").append(logger.getIdentificadorDocumento()).append("]");
        }
        msgShowLog.append("[").append(logger.getOperacion()).append("]");
        msgShowLog.append("[").append(logger.getSubOperacion()).append("]");
        msgShowLog.append("[").append(logger.getNombreClase()).append(".").append(logger.getNombreMetodo()).append("]");
        if (logger.getMensaje() != null) {
            msgShowLog.append("[").append(logger.getMensaje()).append("]");
        }
        if (logger.getParametros() != null) {
            msgShowLog.append("[").append(logger.getParametros()).append("]");
        }
        switch (levelLog) {
            case ERROR:
                if (logger.getExcepcion() != null) {
                    log.error(msgShowLog.toString(), logger.getExcepcion());
                } else {
                    log.error(msgShowLog.toString());
                }
                break;
            case WARNING:
                if (logger.getExcepcion() != null) {
                    log.warn(msgShowLog.toString(), logger.getExcepcion());
                } else {
                    log.warn(msgShowLog.toString());
                }
                break;
            case INFO:
                log.info(msgShowLog.toString());
                break;
            default:
                break;
        }
    }

}

