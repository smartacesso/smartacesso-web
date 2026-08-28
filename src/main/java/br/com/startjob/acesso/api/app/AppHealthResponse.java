package br.com.startjob.acesso.api.app;

public class AppHealthResponse {
    private String status = "ok";
    private boolean jwtConfigured;
    private boolean firebasePathConfigured;
    private boolean firebaseFileExists;
    private boolean firebaseReady;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isJwtConfigured() {
        return jwtConfigured;
    }

    public void setJwtConfigured(boolean jwtConfigured) {
        this.jwtConfigured = jwtConfigured;
    }

    public boolean isFirebasePathConfigured() {
        return firebasePathConfigured;
    }

    public void setFirebasePathConfigured(boolean firebasePathConfigured) {
        this.firebasePathConfigured = firebasePathConfigured;
    }

    public boolean isFirebaseFileExists() {
        return firebaseFileExists;
    }

    public void setFirebaseFileExists(boolean firebaseFileExists) {
        this.firebaseFileExists = firebaseFileExists;
    }

    public boolean isFirebaseReady() {
        return firebaseReady;
    }

    public void setFirebaseReady(boolean firebaseReady) {
        this.firebaseReady = firebaseReady;
    }
}
