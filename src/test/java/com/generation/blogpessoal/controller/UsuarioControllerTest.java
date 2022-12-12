package com.generation.blogpessoal.controller;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.model.UsuarioLogin;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.service.UsuarioService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest (webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance (TestInstance.Lifecycle.PER_CLASS)
public class UsuarioControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @BeforeAll
    void start() {
        usuarioRepository.deleteAll();
        usuarioService.cadastrarUsuario(new Usuario(0L, "Root",
                "root@root.com", "rootroot", " "));
    }

    @Test
    @DisplayName("Cadastrar um Usuário")
    public void deveCriarUmUsuario() {

        HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario(0L,
                "Fulano", "fulano@email.com", "12345678", " "));

        ResponseEntity<Usuario> corpoResposta = testRestTemplate
                .exchange("/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);

        assertEquals(HttpStatus.CREATED, corpoResposta.getStatusCode());
        assertEquals(corpoRequisicao.getBody().getNome(), corpoResposta.getBody().getNome());
        assertEquals(corpoRequisicao.getBody().getUsuario(), corpoResposta.getBody().getUsuario());

    }

    @Test
    @DisplayName("Listar todos os Usuários")
    public void deveMostrarTodosUsuarios() {

        usuarioService.cadastrarUsuario(new Usuario(0L, "Ciclano",
                        "ciclano@email.com", "12345678", " "));
        usuarioService.cadastrarUsuario(new Usuario(0L, "Beltrano",
                        "beltrano@email.com", "12345678", " "));

        ResponseEntity<String> resposta = testRestTemplate
                .withBasicAuth("root@root.com", "rootroot")
                .exchange("/usuarios/all", HttpMethod.GET, null, String.class);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());

    }

    @Test
    @DisplayName("Buscar Usuário por ID")
    public void buscarUsuarioPorId() {

        Optional<Usuario> novoUsuario = usuarioService.cadastrarUsuario(new Usuario(0L, "Gabriel",
                                "gabriel@email.com", "12345678", " "));

        ResponseEntity<Usuario> resposta = testRestTemplate.withBasicAuth("root@root.com", "rootroot" )
                .exchange("/usuarios/" + novoUsuario.get().getId(), HttpMethod.GET, null, Usuario.class);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());

    }

    @Test
    @DisplayName("Login do Usuário")
    public void loginUsuario() {

        HttpEntity<UsuarioLogin> corpoRequisicao = new HttpEntity<>(
                new UsuarioLogin("root@root.com", "rootroot"));

        ResponseEntity<Usuario> corpoResposta = testRestTemplate
                .exchange("/usuarios/logar", HttpMethod.POST, corpoRequisicao, Usuario.class);

        assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());
    }

    @Test
    @DisplayName("Não Permitir Duplicar Usuário")
    public void naoDuplicarUsuario() { usuarioService
            .cadastrarUsuario(new Usuario(0L, "Frida", "frida@email.com", "12345678", ""));

    HttpEntity<Usuario> corpoRequisicao = new HttpEntity<>(new Usuario(0L, "Ana", "ana@email.com", "12345678", ""));

    ResponseEntity<Usuario> corpoResposta = testRestTemplate
            .exchange("/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);

    assertEquals(HttpStatus.BAD_REQUEST, corpoResposta.getStatusCode());
    }
}
