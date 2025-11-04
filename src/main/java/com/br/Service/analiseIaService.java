package com.br.Service;
import com.br.Repository.analiseIaRepository;
import org.springframework.data.jpa.domain.AbstractPersistable_;
import org.springframework.stereotype.Service;
import com.br.Entity.analiseIa;


import java.util.Optional;
import java.util.UUID;

@Service
public class analiseIaService {
    private final analiseIaRepository analiseIaRepository;

    public analiseIaService(analiseIaRepository analiseIaRepository) {
        this.analiseIaRepository = analiseIaRepository;
    }

    public void delete(UUID id){
        analiseIaRepository.deleteById(id);
    }
    public analiseIa atualizarAnalise (UUID id, analiseIa analiseIa){
        Optional<analiseIa> existingAnalise = analiseIaRepository.findById(analiseIa.getId());
        if (existingAnalise.isPresent()){
            analiseIa = existingAnalise.get();
            analiseIa.setRespostaIA(analiseIa.getRespostaIA());
            return analiseIaRepository.save(analiseIa);
        } else {
            throw new RuntimeException("Analise IA não existe!" + AbstractPersistable_.id);
        }
    }

}
