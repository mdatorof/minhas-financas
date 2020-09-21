package com.datoro.minhasfinancas.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.datoro.minhasfinancas.exception.RegraNegocioException;
import com.datoro.minhasfinancas.model.entity.Lancamento;
import com.datoro.minhasfinancas.model.entity.Usuario;
import com.datoro.minhasfinancas.model.enums.StatusLancamento;
import com.datoro.minhasfinancas.model.enums.TipoLancamento;
import com.datoro.minhasfinancas.model.repository.LancamentoRepository;
import com.datoro.minhasfinancas.model.repository.LancamentoRepositoryTest;
import com.datoro.minhasfinancas.service.impl.LancamentoServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SuppressWarnings("unchecked")
public class LancamentoServiceTest {

	@SpyBean
	LancamentoServiceImpl lancamentoService;

	@MockBean
	LancamentoRepository repository;

	// Teste 01 - Deve salvar com sucesso um lançamento na base.
	@Test
	public void deveSalvarUmLancamento() {
		// Cenário
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		Mockito.doNothing().when(lancamentoService).validar(lancamentoASalvar);

		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1L);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		Mockito.when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);

		// Execução
		Lancamento lancamento = lancamentoService.salvar(lancamentoASalvar);

		// Verificação
		assertThat(lancamento.getId()).isEqualTo(lancamentoSalvo.getId());
		assertThat(lancamento.getStatus()).isEqualTo(StatusLancamento.PENDENTE);
	}

	// Teste 02 - Não deve salvar o lançamento quando houver algum erro..
	@Test
	public void naoDeveSalvarUmLancamentoQuandoHouverErroDeValidacao() {
		// Cenário
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		Mockito.doThrow(RegraNegocioException.class).when(lancamentoService).validar(lancamentoASalvar);

		// Execução e verificação.
		catchThrowableOfType(() -> lancamentoService.salvar(lancamentoASalvar), RegraNegocioException.class);
		Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
	}

	// Teste 03 - Deve atualizar com sucesso um lançamento na base.
	@Test
	public void deveAtualizarUmLancamento() {
		// Cenário
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();

		lancamentoSalvo.setId(1L);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);

		Mockito.doNothing().when(lancamentoService).validar(lancamentoSalvo);

		Mockito.when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);

		// Execução
		lancamentoService.atualizar(lancamentoSalvo);

		// Verificação
		Mockito.verify(repository, Mockito.times(1)).save(lancamentoSalvo);
	}

	// Teste 04 - Erro ao tentar atualizar lancamento que não existe.
	@Test
	public void deveLancarErroAoTentarAtualizarUmLancamentoQueNaoFoiSalvo() {
		// Cenário
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();

		// Execução e verificação.
		catchThrowableOfType(() -> lancamentoService.atualizar(lancamentoASalvar), NullPointerException.class);
		Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
	}

	// Teste 05 - Deve deletar um lancamento com sucesso.
	@Test
	public void deveDeletarUmLancamentoComSucesso() {
		// Cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1L);

		// Execução
		lancamentoService.deletar(lancamento);

		// Verificação.
		Mockito.verify(repository).delete(lancamento);
	}

	// Teste 06 - Deve lancar erro ao tentar deletar um lancamento não salvo.
	@Test
	public void deveLancarErroAoTendarDeletarUmLancamentoAindaNaoSalvo() {
		// Cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();

		// Execução
		catchThrowableOfType(() -> lancamentoService.deletar(lancamento), NullPointerException.class);

		// Verificação.
		Mockito.verify(repository, Mockito.never()).delete(lancamento);
	}

	// Teste 07 - Deve filtrar lancamentos.
	@Test
	public void deveFiltrarLancamentos() {
		// Cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1L);
		List<Lancamento> lista = Arrays.asList(lancamento);
		Mockito.when(repository.findAll(Mockito.any(Example.class))).thenReturn(lista);

		// Execução
		List<Lancamento> resultado = lancamentoService.buscar(lancamento);

		// Verificações
		assertThat(resultado).isNotEmpty().hasSize(1).contains(lancamento);
	}

	// Teste 08 - Atualizar status.
	@Test
	public void deveAtualizarOStatusDeUmLancamento() {
		// Cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1L);
		lancamento.setStatus(StatusLancamento.PENDENTE);

		StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
		Mockito.doReturn(lancamento).when(lancamentoService).atualizar(lancamento);

		// Execução
		lancamentoService.atualizarStatus(lancamento, novoStatus);

		// Verificação
		assertThat(lancamento.getStatus()).isEqualTo(novoStatus);
		Mockito.verify(lancamentoService).atualizar(lancamento);
	}

	// Teste 09 - Deve obter um lancamento por Id.
	@Test
	public void deveObterUmLancamentoPorId() {
		// Cenário
		Long id = 1L;

		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);

		Mockito.when(repository.findById(id)).thenReturn(Optional.of(lancamento));

		// Execução
		Optional<Lancamento> resultado = lancamentoService.obterPorId(id);

		// Verificação
		assertThat(resultado.isPresent()).isTrue();
	}

	// Teste 10 - Deve retornar vazio quando o lancamento não existe.
	@Test
	public void deveRetornarVazioQuandoOLancamentoNaoExiste() {
		// Cenário
		Long id = 1L;

		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);

		Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

		// Execução
		Optional<Lancamento> resultado = lancamentoService.obterPorId(id);

		// Verificação
		assertThat(resultado.isPresent()).isFalse();
	}

	// Teste 11 - Deve lançar exceções ao validar lançamentos.
	@Test
	public void deveLancarErrosAoValidarUmLancamento() {

		Lancamento lancamento = new Lancamento();

		Throwable erro = catchThrowable(() -> lancamentoService.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma Descrição válida!");

		lancamento.setDescricao("");

		erro = catchThrowable(() -> lancamentoService.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma Descrição válida!");

		lancamento.setDescricao("Salário");

		erro = catchThrowable(() -> lancamentoService.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido!");

		lancamento.setMes(0);

		erro = catchThrowable(() -> lancamentoService.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido!");

		lancamento.setMes(13);

		erro = catchThrowable(() -> lancamentoService.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido!");

		lancamento.setMes(1);

		erro = catchThrowable(() -> lancamentoService.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano válido!");

		lancamento.setAno(202);

		erro = catchThrowable(() -> lancamentoService.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano válido!");

		lancamento.setAno(2020);

		erro = catchThrowable(() -> lancamentoService.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuário!");

		lancamento.setUsuario(new Usuario());

		erro = catchThrowable(() -> lancamentoService.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuário!");

		lancamento.getUsuario().setId(1L);

		erro = catchThrowable(() -> lancamentoService.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Valor válido!");

		lancamento.setValor(BigDecimal.ZERO);

		erro = catchThrowable(() -> lancamentoService.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Valor válido!");

		lancamento.setValor(BigDecimal.valueOf(1));

		erro = catchThrowable(() -> lancamentoService.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um tipo de Lançamento!");
	}
	
	@Test
	public void deveObterSaldoPorUsuarioTipoLancamentoEStatusLancamento() {
		//cenario
		Long idUsuario = 1L;
		
		Mockito.when( repository
				.obterSaldoPorTipoLancamentoEUsuarioEStatus(idUsuario, TipoLancamento.RECEITA, StatusLancamento.EFETIVADO)) 
				.thenReturn(BigDecimal.valueOf(100));
		
		Mockito.when( repository
				.obterSaldoPorTipoLancamentoEUsuarioEStatus(idUsuario, TipoLancamento.DESPESA, StatusLancamento.EFETIVADO)) 
				.thenReturn(BigDecimal.valueOf(50));
		
		//execucao
		BigDecimal saldo = lancamentoService.obterSaldoPorUsuarioTipoLancamentoEStatusLancamento(idUsuario);
		
		//verificacao
		assertThat(saldo).isEqualTo(BigDecimal.valueOf(50));
	}
}
