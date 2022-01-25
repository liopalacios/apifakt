package pe.com.certifakt.apifact.util;

import com.google.gson.Gson;

public class AgregarPilaToError {

	 public static void put(Exception ex, Object request) {
        try {
            StackTraceElement[] stackTraceElements = new StackTraceElement[ex.getStackTrace().length + 1];
            int i = 0;
            for (StackTraceElement stackTraceElement : ex.getStackTrace()) {
                stackTraceElements[i++] = stackTraceElement;
            }
            StackTraceElement element = new StackTraceElement(new Gson().toJson(request), "", "jsonRequest", -1);
            stackTraceElements[i] = element;
            ex.setStackTrace(stackTraceElements);
        } catch (Exception e) {
            e.printStackTrace();
        }
	 }

    public static void put(Exception ex, Object request, String name) {
        try {
            StackTraceElement[] stackTraceElements = new StackTraceElement[ex.getStackTrace().length + 1];
            int i = 0;
            for (StackTraceElement stackTraceElement : ex.getStackTrace()) {
                stackTraceElements[i++] = stackTraceElement;
            }
            StackTraceElement element = new StackTraceElement(new Gson().toJson(request), "", (name == null || name.trim().isEmpty())?"jsonRequest":name, -1);
            stackTraceElements[i] = element;
            ex.setStackTrace(stackTraceElements);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

}
