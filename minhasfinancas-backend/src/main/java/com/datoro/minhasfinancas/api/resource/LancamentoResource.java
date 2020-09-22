package com.datoro.minhasfinancas.api.resource;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.datoro.minhasfinancas.api.dto.AtualizaStatusDTO;
import com.datoro.minhasfinancas.api.dto.LancamentoDTO;
import com.datoro.minhasfinancas.exception.RegraNegocioException;
import com.datoro.minhasfinancas.model.entity.Lancamento;
import com.datoro.minhasfinancas.model.entity.Usuario;
import com.datoro.minhasfinancas.model.enums.StatusLancamento;
import com.datoro.minhasfinancas.model.enums.TipoLancamento;
import com.datoro.minhasfinancas.service.LancamentoService;
import com.datoro.minhasfinancas.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/lancamentos")
@RequiredArgsConstructor
@SuppressWarnings({ "rawtypes", "unchecked" })
public class LancamentoResource {

	private final LancamentoService lancamentoService;
	private final UsuarioService usuarioService;

	@GetMapping
	public ResponseEntity<Object> buscar(@RequestParam(value = "descricao", required = false) String descricao,
			@RequestParam(value = "mes", required = false) Integer mes,
			@RequestParam(value = "ano", required = false) Integer ano,
			@RequestParam(value = "tipo", required = false) TipoLancamento tipo,
			@RequestParam(value = "status", required = false) StatusLancamento status,
			@RequestParam(value = "usuario") Long idUsuario) {

		Lancamento lancamentoFiltro = new Lancamento();
		lancamentoFiltro.setDescricao(descricao);
		lancamentoFiltro.setMes(mes);
		lancamentoFiltro.setAno(ano);
		lancamentoFiltro.setTipo(tipo);
		lancamentoFiltro.setStatus(status);

		Optional<Usuario> usuario = usuarioService.obterPorId(idUsuario);
		if (!usuario.isPresent()) {
			return ResponseEntity.badRequest()
					.body("Não foi possível realizar a consulta. Usuário não cadastrado para o Id informado.");
		} else {
			lancamentoFiltro.setUsuario(usuario.get());
		}

		List<Lancamento> lancamentos = lancamentoService.buscar(lancamentoFiltro);
		return ResponseEntity.ok(lancamentos);
	}
	
	@GetMapping("{id}")
	public ResponseEntity<Serializable> obterLancamento(@PathVariable("id") Long id) {
		return lancamentoService.obterPorId(id)
				      .map(lancamento -> new ResponseEntity(converter(lancamento), HttpStatus.OK))
				      .orElseGet(() -> new ResponseEntity(HttpStatus.NOT_FOUND));
	}

	@PostMapping
	public ResponseEntity<Serializable> salvar(@RequestBody LancamentoDTO dto) {

		try {
			Lancamento entidade = converter(dto);
			lancamentoService.salvar(entidade);
			return new ResponseEntity<Serializable>(entidade, HttpStatus.CREATED);
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PutMapping("{id}")
	public ResponseEntity<? extends Serializable> atualizar(@PathVariable("id") Long id,
			@RequestBody LancamentoDTO dto) {
		return lancamentoService.obterPorId(id).map(entity -> {
			try {
				Lancamento lancamento = converter(dto);
				lancamento.setId(entity.getId());
				lancamentoService.atualizar(lancamento);
				return ResponseEntity.ok(lancamento);
			} catch (RegraNegocioException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
		}).orElseGet(() -> new ResponseEntity<String>("Lancamento não encontrado na base de dados.",
				HttpStatus.BAD_REQUEST));
	}

	@PutMapping("{id}/atualiza-status")
	public ResponseEntity atualizarStatus(@PathVariable("id") Long id, @RequestBody AtualizaStatusDTO dto) {
		return lancamentoService.obterPorId(id).map(entity -> {
			StatusLancamento statusSelecionado = StatusLancamento.valueOf(dto.getStatus());
			
			if(statusSelecionado == null) {
				return ResponseEntity.badRequest().body("Não foi possível atualizar o status do lancamento, envie um status válido.");
			}
			
			try {
				entity.setStatus(statusSelecionado);
				lancamentoService.atualizar(entity);
				return ResponseEntity.ok(entity);
			} catch (RegraNegocioException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
		}).orElseGet( () ->
			new ResponseEntity("Lancamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST));
	}

	@DeleteMapping("{id}")
	public ResponseEntity<?> deletar(@PathVariable("id") Long id) {
		return lancamentoService.obterPorId(id).map(entity -> {
			lancamentoService.deletar(entity);
			return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
		}).orElseGet(() -> new ResponseEntity<Object>("Lancamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST));
	}
	
	private LancamentoDTO converter(Lancamento lancamento) {
		return LancamentoDTO.builder()
							.id(lancamento.getId())
							.descricao(lancamento.getDescricao())
							.valor(lancamento.getValor())
							.mes(lancamento.getMes())
							.ano(lancamento.getAno())
							.status(lancamento.getStatus().name())
							.tipo(lancamento.getTipo().name())
							.usuario(lancamento.getUsuario().getId())
							.build();
	}

	private Lancamento converter(LancamentoDTO dto) {
		Lancamento lancamento = new Lancamento();
		lancamento.setId(dto.getId());
		lancamento.setDescricao(dto.getDescricao());
		lancamento.setAno(dto.getAno());
		lancamento.setMes(dto.getMes());
		lancamento.setValor(dto.getValor());

		Usuario usuario = usuarioService.obterPorId(dto.getUsuario())
				.orElseThrow(() -> new RegraNegocioException("Usuario não encontrado para o Id informado."));

		lancamento.setUsuario(usuario);

		if (dto.getTipo() != null) {
			lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));
		}

		if (dto.getStatus() != null) {
			lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));
		}

		return lancamento;
	}
}
