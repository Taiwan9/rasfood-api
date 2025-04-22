package com.rasmoo.api.rasfood.controller;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rasmoo.api.rasfood.dto.CardapioDto;
import com.rasmoo.api.rasfood.entity.Cardapio;
import com.rasmoo.api.rasfood.repository.CardapioRepository;
import com.rasmoo.api.rasfood.repository.projection.CardapioProjection;
import com.rasmoo.api.rasfood.repository.specification.CardapioSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequestMapping("/cardapio")
@RestController
public class CardapioController {

    @Autowired
    private CardapioRepository cardapioRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping
    ResponseEntity<Page<Cardapio>> consultaTodos(@RequestParam("page") Integer page, @RequestParam("size") Integer size,
                                                 @RequestParam(value = "sort", required = false) Sort.Direction sort,
                                                 @RequestParam(value = "property", required = false) String property){
        Pageable pageable= Objects.nonNull(sort)
                ?PageRequest.of(page,size, Sort.by(sort, property))
                :PageRequest.of(page,size);
        return ResponseEntity.status(HttpStatus.OK).body(cardapioRepository.findAll(pageable));
    }

    @GetMapping("/nome/{nome}/disponivel")
    ResponseEntity<List<Cardapio>> consultaPorNome(@PathVariable("nome") final String nome,
                                                   @RequestParam("page") Integer page, @RequestParam("size") Integer size){
        Pageable pageable= PageRequest.of(page,size);
        return ResponseEntity.status(HttpStatus.OK).body(cardapioRepository.findAll(
                Specification
                        .where(CardapioSpec.nome(nome)
                                .and(CardapioSpec.disponivel(true))),
                pageable).getContent());
    }

    @GetMapping("/categoria/{categoriaId}/disponivel")
    ResponseEntity<Page<CardapioProjection>> consultaPorCategoriaEDisponibilidade(@PathVariable("categoriaId") final Integer categoriaId,
        @RequestParam("page") Integer page, @RequestParam("size") Integer size){
        Pageable pageable= PageRequest.of(page,size);
        return ResponseEntity.status(HttpStatus.OK).body(cardapioRepository.findAllByCategoria(categoriaId, pageable));
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
    @PatchMapping(path = "/{id}/img", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<Cardapio> salvarImg(@PathVariable("id") final Integer id, @RequestPart final MultipartFile file) throws IOException {
        Optional<Cardapio> cardapioEncontrado = this.cardapioRepository.findById(id);

        if(cardapioEncontrado.isPresent()){
            Cardapio cardapio = cardapioEncontrado.get();
             cardapio.setImg(file.getBytes());
            return ResponseEntity.status(HttpStatus.OK).body(this.cardapioRepository.save(cardapioEncontrado.get()));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @PostMapping
    ResponseEntity<Cardapio> criar(@RequestBody final Cardapio cardapio) throws JsonMappingException {
        return ResponseEntity.status(HttpStatus.CREATED).body(cardapioRepository.save(cardapio));
    }

}
