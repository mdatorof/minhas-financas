package com.datoro.minhasfinancas.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.datoro.minhasfinancas.exception.ErroAutenticacao;
import com.datoro.minhasfinancas.exception.RegraNegocioException;
import com.datoro.minhasfinancas.model.entity.Usuario;
import com.datoro.minhasfinancas.model.repository.UsuarioRepository;
import com.datoro.minhasfinancas.service.impl.UsuarioServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

	@SpyBean
	UsuarioServiceImpl service;

	@MockBean
	UsuarioRepository repository;


	//Teste Usuario: 1  (Salvar usuario com sucesso)
	@Test
	public void deveSalvarUmUsuario() {
		//cenário
		Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
		
		Usuario usuario = Usuario.builder().id(1L).nome("nome").email("email@gmail.com").senha("senha").build();
		
		Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);
		
		//ação
		Usuario usuarioSalvo = assertDoesNotThrow(() ->service.salvarUsuario(new Usuario()));
		
		//verificação
		Assertions.assertThat(usuarioSalvo).isNotNull();
		Assertions.assertThat(usuarioSalvo.getId()).isEqualTo(1L);
		Assertions.assertThat(usuarioSalvo.getNome()).isEqualTo("nome");
		Assertions.assertThat(usuarioSalvo.getEmail()).isEqualTo("email@gmail.com");
		Assertions.assertThat(usuarioSalvo.getSenha()).isEqualTo("senha");
	}
	
	//Teste Usuario: 2  (nao deve salvar usuario com sucesso)
		@Test
		public void naoDeveSalvarUmUsuarioComEmailJaCadastrado() {
			//cenário
			String email = "email@gmail.com";
			Usuario usuario = Usuario.builder().email(email).build();
			Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(email);
			
			//ação
			assertThrows(RegraNegocioException.class, () -> service.salvarUsuario(usuario));
			
			//verificação
			Mockito.verify(repository, Mockito.never()).save(usuario);
		}
	
	// Teste Usuario: 3 (Autentica o usuario com sucesso).
	@Test
	public void deveAutenticarUmUsuarioComSucesso() {
		// cenário
		String email = "email@gmail.com";
		String senha = "senha";

		Usuario usuario = Usuario.builder().email(email).senha(senha).id(1L).build();
		Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));
		// ação
		Usuario result = assertDoesNotThrow(() -> service.autenticar(email, senha));

		// verificação
		assertNotNull(result);
	}

	// Teste Usuario: 4 (Erro de usuario nao encontrado).
	@Test
	public void deveLancarErroQuandoNaoEncontrarUsuarioCadastradoComOEmailInformado() {
		// cenário
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

		// ação
		Throwable exception = Assertions.catchThrowable(() -> service.autenticar("email@gmail.com", "senha"));

		// Verificação
		Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class)
				.hasMessage("Usuário não encontrado para o email informado!");
	}

	// Teste Usuario: 3 (Encontra usuario mas a senha não bate).
	@Test
	public void deveLancarErroQuandoEncontrarUsuarioCadastradoComOEmailInformadoMasSenhaNaoConfere() {
		// cenário

		String email = "email@gmail.com";
		String senha = "senha";

		Usuario usuario = Usuario.builder().email(email).senha(senha).build();
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));

		// ação
		Throwable exception = Assertions.catchThrowable(() -> service.autenticar("email@gmail.com", "1234"));

		// Verificação
		Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Senha inválida!");
	}

	// Teste Email: 1 (Verifica se o email esta cadastrado na base de dados o mesmo
	// não deve estar).
	@Test
	public void deveValidarEmail() {

		// cenário
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);

		// ação
		assertDoesNotThrow(() -> service.validarEmail("email@gmail.com"));
	}

	// Teste Email: 2 (Acusa erro ao validar email e o mesmo ja estiver cadastrado
	// na base de dados).
	@Test
	public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado() {

		// cenário
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);

		// ação

		assertThrows(RegraNegocioException.class, () -> service.validarEmail("email@gmail.com"));
	}
}