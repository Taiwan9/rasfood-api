package com.rasmoo.api.rasfood.controller;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rasmoo.api.rasfood.dto.CardapioDto;
import com.rasmoo.api.rasfood.entity.Cardapio;
import com.rasmoo.api.rasfood.repository.CardapioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequestMapping("/cardapio")
@RestController
public class CardapioController {

    @Autowired
    private CardapioRepository cardapioRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping
    ResponseEntity<List<Cardapio>> consultaTodos(){
        return ResponseEntity.status(HttpStatus.OK).body(cardapioRepository.findAll());
    }

    @GetMapping("/nome/{nome}/disponivel")
    ResponseEntity<List<CardapioDto>> consultaPorNome(@PathVariable("nome") final String nome){
        return ResponseEntity.status(HttpStatus.OK).body(cardapioRepository.findAllByNome(nome));
    }

    @GetMapping("/categoria/{categoriaId}/disponivel")
    ResponseEntity<List<Cardapio>> consultaPorCategoriaEDisponibilidade(@PathVariable("categoriaId") final Integer categoriaId){
        return ResponseEntity.status(HttpStatus.OK).body(cardapioRepository.findAllByCategoria(categoriaId));
    }


    @GetMapping("/{id}")
    ResponseEntity<Cardapio> consultaPorId(@PathVariable("id") final Integer id){
        Optional<Cardapio> cardapioEncontrado = cardapioRepository.findById(id);
        return cardapioEncontrado.map(cardapio -> ResponseEntity.status(HttpStatus.OK)
                .body(this.cardapioRepository.save(cardapio))).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> excluirPorId(@PathVariable("id") final Integer id){
        Optional<Cardapio> cardapioEncontrado = cardapioRepository.findById(id);
        if(cardapioEncontrado.isPresent()){
            cardapioRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @PatchMapping("/{id}")
    ResponseEntity<Cardapio> atualizar(@PathVariable("id") final Integer id, @RequestBody final Cardapio cardapio) throws JsonMappingException {
        Optional<Cardapio> cardapioEncontrado = this.cardapioRepository.findById(id);

        if(cardapioEncontrado.isPresent()){
            objectMapper.updateValue(cardapioEncontrado.get(), cardapio);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(this.cardapioRepository.save(cardapioEncontrado.get()));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @PostMapping
    ResponseEntity<Cardapio> criar(@RequestBody final Cardapio cardapio) throws JsonMappingException {
        return ResponseEntity.status(HttpStatus.CREATED).body(cardapioRepository.save(cardapio));
    }

}
