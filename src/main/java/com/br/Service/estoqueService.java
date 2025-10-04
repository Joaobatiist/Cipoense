package com.br.Service;

import com.br.Entity.estoque;
import com.br.Repository.estoqueRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.domain.AbstractPersistable_;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class estoqueService {


    private final estoqueRepository estoqueRepository;


    public estoqueService(estoqueRepository estoqueRepository) {
        this.estoqueRepository = estoqueRepository;
    }
    @Transactional
    public estoque cadastrarItem(estoque estoque) {
        return estoqueRepository.save(estoque);
    }

   public List<estoque> listarEstoques(){
        return estoqueRepository.findAll();
   }

   @Transactional
   public estoque atualizarItem(UUID id, estoque estoque){
        Optional<estoque> existingEstoque = estoqueRepository.findById(estoque.getId());
        if (existingEstoque.isPresent()) {
            estoque estoqueAtualizado = existingEstoque.get();
            estoqueAtualizado.setNome(estoque.getNome());
            estoqueAtualizado.setQuantidade(estoque.getQuantidade());
           return estoqueRepository.save(estoqueAtualizado);
        } else {
            throw new RuntimeException("Item não encontrado" + AbstractPersistable_.id);
        }
   }

    @Transactional
   public void deletarItem(UUID id){
        if (!estoqueRepository.existsById(id)) {
            throw new RuntimeException("Item não encontrado" + id );
        }
        estoqueRepository.deleteById(id);
   }

   public Optional<estoque> buscaEstoque(UUID id){
        return estoqueRepository.findById(id);
   }

}
