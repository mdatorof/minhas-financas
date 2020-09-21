package com.datoro.minhasfinancas.model.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.datoro.minhasfinancas.model.entity.Lancamento;
import com.datoro.minhasfinancas.model.enums.StatusLancamento;
import com.datoro.minhasfinancas.model.enums.TipoLancamento;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class LancamentoRepositoryTest {

	@Autowired
	LancamentoRepository repository;

	@Autowired
	TestEntityManager entityManager;

	// Teste 01 - Salvar um lançamento na base.
	@Test
	public void deveSalvarUmLancamento() {

		Lancamento lancamento = criarLancamento();

		lancamento = repository.save(lancamento);

		assertNotNull(lancamento.getId()); 
	}

	// Teste 02 - Deletar um lançamento da base.
	@Test
	public void deveDeletarUmLancamento() {

		Lancamento lancamento = criarEPersistirUmLancamento();
		lancamento = entityManager.find(Lancamento.class, lancamento.getId());
		repository.delete(lancamento);
		Lancamento lancamentoInesistente = entityManager.find(Lancamento.class, lancamento.getId());
		assertThat(lancamentoInesistente).isNull(); 
	}

	// Teste 03 - Atualiza os dados de um lançamento.
	@Test
	public void deveAtualizarUmLancamento() {
		Lancamento lancamento = criarEPersistirUmLancamento();
		lancamento.setAno(2019);
		lancamento.setDescricao("Teste de atualização de lançamento");
		lancamento.setStatus(StatusLancamento.CANCELADO);

		repository.save(lancamento);

		Lancamento lancamentoAtualizado = entityManager.find(Lancamento.class, lancamento.getId());

		assertThat(lancamentoAtualizado.getAno()).isEqualTo(2019);
		assertThat(lancamentoAtualizado.getDescricao()).isEqualTo("Teste de atualização de lançamento");
		assertThat(lancamentoAtualizado.getStatus()).isEqualTo(StatusLancamento.CANCELADO);
	}

	// Teste 04 - Pesquisa um lançamento por Id.
	@Test
	public void devePesquisarUmLancamentoPorId() {
		Lancamento lancamento = criarEPersistirUmLancamento();

		Optional<Lancamento> lancamentoEncontrado = repository.findById(lancamento.getId());

		assertThat(lancamentoEncontrado.isPresent()).isTrue();
	}

	public static Lancamento criarLancamento() {
		return Lancamento.builder().ano(2020).mes(8).descricao("Lancamento de teste").valor(BigDecimal.valueOf(10))
				.tipo(TipoLancamento.RECEITA).dataCadastro(LocalDate.now()).build();
	}

	private Lancamento criarEPersistirUmLancamento() {
		Lancamento lancamento = criarLancamento();
		entityManager.persist(lancamento);
		return lancamento;
	}
}
