package com.br.Service;

import com.br.Entity.Estoque;
import com.br.Repository.EstoqueRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.domain.AbstractPersistable_;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EstoqueService {


    private final EstoqueRepository estoqueRepository;


    public EstoqueService(EstoqueRepository estoqueRepository) {
        this.estoqueRepository = estoqueRepository;
    }
    @Transactional
    public Estoque cadastrarItem( Estoque estoque) {
        return estoqueRepository.save(estoque);
    }

   public List<Estoque> listarEstoques(){
        return estoqueRepository.findAll();
   }

   @Transactional
   public Estoque atualizarItem(Long id, Estoque estoque){
        Optional<Estoque> existingEstoque = estoqueRepository.findById(estoque.getId());
        if (existingEstoque.isPresent()) {
            Estoque estoqueAtualizado = existingEstoque.get();
            estoqueAtualizado.setNome(estoque.getNome());
            estoqueAtualizado.setQuantidade(estoque.getQuantidade());
           return estoqueRepository.save(estoqueAtualizado);
        } else {
            throw new RuntimeException("Item não encontrado" + AbstractPersistable_.id);
        }
   }

    @Transactional
   public void deletarItem(Long id){
        if (!estoqueRepository.existsById(id)) {
            throw new RuntimeException("Item não encontrado" + id );
        }
        estoqueRepository.deleteById(id);
   }

   public Optional<Estoque> buscaEstoque(Long id){
        return estoqueRepository.findById(id);
   }

}
