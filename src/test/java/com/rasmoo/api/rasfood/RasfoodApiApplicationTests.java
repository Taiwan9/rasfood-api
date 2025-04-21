package com.rasmoo.api.rasfood;

import com.rasmoo.api.rasfood.repository.CardapioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RasfoodApiApplicationTests {

	@Autowired
	private CardapioRepository cardapioRepository;

	@Test
	void contextLoads() {
		//this.cardapioRepository.updateDisponibilidade(2);
	}

}
