package br.com.startjob.acesso.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity implements Serializable {

    @Transient
    private Boolean existente = Boolean.FALSE;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATA_ALTERACAO", nullable = false)
    private Date dataAlteracao = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATA_CRIACAO", nullable = false)
    private Date dataCriacao = new Date();

    @Version
    @Column(name = "VERSAO", nullable = false)
    private Integer versao = 0;

    @Column(name = "REMOVIDO")
    private Boolean removido;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATA_REMOVIDO")
    private Date dataRemovido;

    @Transient
    private boolean alterado = false;

    @PrePersist
    protected void onCreate() {
        Date now = new Date();
        if (dataCriacao == null) {
            dataCriacao = now;
        }
        dataAlteracao = now;
    }

    @PreUpdate
    protected void onUpdate() {
        dataAlteracao = new Date();
    }

    public boolean isAtivoLogicamente() {
        return removido == null || Boolean.FALSE.equals(removido);
    }
}
