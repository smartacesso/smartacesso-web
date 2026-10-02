package br.com.startjob.acesso.dataprovider.entity;

import br.com.startjob.acesso.core.enumeration.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "TB_PLANO")
public class PlanoEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "ID_PLANO", nullable = false)
    private Long id;

    @Column(name = "NOME", length = 255)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", length = 10)
    private Status status;

    @Column(name = "DATA_INICIO")
    private LocalDateTime inicio;

    @Column(name = "DATA_FIM")
    private LocalDateTime fim;

    @Column(name = "PERIODICIDADE", length = 255)
    private String periodicidadeCobranca;

    @Column(name = "DIA_VENCIMENTO")
    private Long diaVencimento;

    @Column(name = "VALOR")
    private Double valor;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CLIENTE")
    private ClienteEntity cliente;
}
