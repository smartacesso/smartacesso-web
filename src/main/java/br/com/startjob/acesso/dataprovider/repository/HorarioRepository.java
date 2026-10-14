package br.com.startjob.acesso.dataprovider.repository;

import br.com.startjob.acesso.dataprovider.entity.HorarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HorarioRepository extends JpaRepository<HorarioEntity, Long> {

    @Query("select h from HorarioEntity h where h.regra.id = :idRegra and (h.removido = false or h.removido is null)")
    List<HorarioEntity> findAllActiveByIdRegra(@Param("idRegra") Long idRegra);
}
