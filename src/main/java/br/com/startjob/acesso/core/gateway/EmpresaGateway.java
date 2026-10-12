package br.com.startjob.acesso.core.gateway;

import br.com.startjob.acesso.core.domain.empresa.Empresa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface EmpresaGateway {

    Page<Empresa> buscarEmpresas(Long idCliente, String nome, String cnpj, Pageable pageable);

    Optional<Empresa> findById(Long id);

    Empresa save(Empresa empresa);
}
