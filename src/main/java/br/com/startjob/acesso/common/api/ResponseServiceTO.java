package br.com.startjob.acesso.common.api;

import java.io.Serializable;

/**
 * Envelope JAX-RS legado. Campo {@code object} permanece para o desktop Swing.
 */
public class ResponseServiceTO implements Serializable {

    private String status;
    private String message;
    private Serializable object;

    public ResponseServiceTO() {
    }

    public ResponseServiceTO(String status, String message, Serializable object) {
        this.status = status;
        this.message = message;
        this.object = object;
    }

    public static ResponseServiceTO ok(Serializable object) {
        return new ResponseServiceTO("OK", null, object);
    }

    public static ResponseServiceTO error(String httpStatus, String message) {
        return new ResponseServiceTO(httpStatus, message, null);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Serializable getObject() {
        return object;
    }

    public void setObject(Serializable object) {
        this.object = object;
    }
}
