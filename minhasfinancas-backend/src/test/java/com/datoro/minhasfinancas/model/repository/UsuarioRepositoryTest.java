package com.datoro.minhasfinancas.model.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.datoro.minhasfinancas.model.entity.Usuario;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UsuarioRepositoryTest {
	
	@Autowired
	UsuarioRepository repository;
	
	@Autowired
	TestEntityManager entityManager;
	
	//Testes com a camada repository - Testes integrados com a base de dados.
	
	//Teste: 1 (Verifica a existencia de um email).
	@Test
	public void deveVerificarAExistenciaDeUmEmail() {
		
		//Cenário
		
	    Usuario usuario = criarUsuario();
	    entityManager.persist(usuario);
		
		//Acãoxecucão
	    
		boolean result = repository.existsByEmail("usuario@gmail.com");
		
		//Verificação
		
		Assertions.assertThat(result).isTrue();
	}
	
	//Teste: 2 (Retorna falso quando não existir usuario cadastrado com o email pesquisado).
	@Test
	public void deveRetornarFalsoQuandoNaoHouverUsuarioCadastradoComOEmail() {
		
		//Cenário
		//Não existe - Base deve estar vazia.
		
		//Acãoxecucão
	    
		boolean result = repository.existsByEmail("usuario@gmail.com");
		
		//Verificação
		
		Assertions.assertThat(result).isFalse();
	}
	
	//Teste: 3 (Persiste usuario na base de dados).
	@Test
	public void devePersistirUmUsuarioNaBaseDeDados() {
		
		//cenário
		Usuario usuario = criarUsuario();
		
		//ação
		Usuario usuarioSalvo = repository.save(usuario);
		
		//verificação
		Assertions.assertThat(usuarioSalvo.getId()).isNotNull();
	}
	
	//Teste: 4	(Pesquisa um usuario por email na base de dados).
	@Test
	public void deveBuscarUmUsuarioPorEmail() {
		
		//cenário
		Usuario usuario = criarUsuario();
		entityManager.persist(usuario);
		
		//verificação
		Optional<Usuario> result = repository.findByEmail("usuario@gmail.com");
		Assertions.assertThat(result.isPresent()).isTrue();
	}
	
	//Teste: 5 (Retorna pesquisa vazia quando pesquisar usuario por emaill e o mesmo não existir).
	@Test
	public void deveRetornarVazioAoBuscarUmUsuarioPorEmailQuandoOMesmoNaoExisteNaBase() {
		
		//cenário
		//Não existe - Base deve estar vazia.

		
		//verificação
		Optional<Usuario> result = repository.findByEmail("usuario@gmail.com");
		Assertions.assertThat(result.isPresent()).isFalse();
	}
	
	
	//Cria um usuario a ser utilizado no teste quando nescessário.
	public static Usuario criarUsuario() {
		return Usuario.builder().nome("usuario").email("usuario@gmail.com").senha("senha").build();
	}
}
