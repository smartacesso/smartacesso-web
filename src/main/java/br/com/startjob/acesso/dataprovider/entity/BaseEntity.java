package br.com.startjob.acesso.dataprovider.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.TrueFalseConverter;

import java.io.Serializable;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.UUID;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Setter
@Getter
@MappedSuperclass
public class BaseEntity implements Serializable {

    @UpdateTimestamp
    @Column(name = "DATA_ALTERACAO", nullable = false)
    private LocalDateTime dataAlteracao;

    @CreationTimestamp
    @Column(name = "DATA_CRIACAO", nullable = false)
    private LocalDateTime dataCriacao;

    @Version
    @Column(name = "VERSAO", columnDefinition = "integer DEFAULT 0", nullable = false)
    private Integer versao;

    @Convert(converter = TrueFalseConverter.class)
    @Column(name = "REMOVIDO")
    private Boolean removido;

    @Column(name = "DATA_REMOVIDO")
    private LocalDateTime dataRemovido;

    @JdbcTypeCode(Types.VARCHAR)
    private UUID uniqueId;

    @PrePersist
    public void prePersist() {
        if (this.uniqueId == null) {
            this.uniqueId = UUID.randomUUID();
        }

        if (this.removido == null) {
            this.removido = false;
        }
    }

    public boolean isAtivoLogicamente() {
        return removido == null || !removido;
    }
}
