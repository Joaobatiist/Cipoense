package com.br.Service;

import com.br.Repository.analiseIaRepository;
import org.springframework.stereotype.Service;


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
}
