package br.com.fiap3ess.rotaseca.exception;

import br.com.fiap3ess.rotaseca.exception.type.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

/**
 * Todas as respostas de erro saem como {"erro": {"codigo": "...", "mensagem": "..."}},
 * formato que o app mobile lê. Erros do cliente são 4xx (o app mostra o erro);
 * 5xx o app trata como "serviço fora do ar" e usa o cache.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CruzamentoNotFoundException.class)
    public ResponseEntity<DadosErro> tratarCruzamentoNotFound(CruzamentoNotFoundException e) {
        return erro(HttpStatus.NOT_FOUND, "local_nao_encontrado", e.getMessage());
    }

    @ExceptionHandler({RecursoNotFoundException.class, EntityNotFoundException.class, NoResourceFoundException.class})
    public ResponseEntity<DadosErro> tratarNotFound(Exception e) {
        return erro(HttpStatus.NOT_FOUND, "nao_encontrado",
                e instanceof RecursoNotFoundException ? e.getMessage() : "Recurso não encontrado.");
    }

    @ExceptionHandler(ParametroInvalidoException.class)
    public ResponseEntity<DadosErro> tratarParametroInvalido(ParametroInvalidoException e) {
        return erro(HttpStatus.BAD_REQUEST, "parametro_invalido", e.getMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<DadosErro> tratarParametroAusente(MissingServletRequestParameterException e) {
        return erro(HttpStatus.BAD_REQUEST, "parametro_invalido", e.getParameterName() + " é obrigatório.");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<DadosErro> tratarTipoInvalido(MethodArgumentTypeMismatchException e) {
        return erro(HttpStatus.BAD_REQUEST, "parametro_invalido", e.getName() + " com valor inválido.");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<DadosErro> tratarCorpoInvalido() {
        return erro(HttpStatus.BAD_REQUEST, "parametro_invalido", "Corpo da requisição ausente ou mal formado.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DadosErro> tratarBadRequest(MethodArgumentNotValidException e) {
        List<DadosCampo> campos = e.getFieldErrors().stream().map(DadosCampo::new).toList();
        return ResponseEntity.badRequest()
                .body(new DadosErro(new Erro("validacao", "Dados inválidos.", campos)));
    }

    @ExceptionHandler(ValidacaoException.class)
    public ResponseEntity<DadosErro> tratarValidacao(ValidacaoException e) {
        return erro(HttpStatus.BAD_REQUEST, "validacao", e.getMessage());
    }

    @ExceptionHandler(ReporteDuplicadoException.class)
    public ResponseEntity<DadosErro> tratarReporteDuplicado(ReporteDuplicadoException e) {
        return erro(HttpStatus.CONFLICT, "reporte_duplicado", e.getMessage());
    }

    @ExceptionHandler(ConflitoException.class)
    public ResponseEntity<DadosErro> tratarConflito(ConflitoException e) {
        return erro(HttpStatus.CONFLICT, "conflito", e.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<DadosErro> tratarIntegridade() {
        return erro(HttpStatus.CONFLICT, "conflito", "Operação viola uma restrição do banco de dados.");
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<DadosErro> tratarAutenticacao() {
        return erro(HttpStatus.UNAUTHORIZED, "credenciais_invalidas", "Login ou senha inválidos.");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<DadosErro> tratarAcessoNegado() {
        return erro(HttpStatus.FORBIDDEN, "acesso_negado", "Seu perfil não tem acesso a este recurso.");
    }

    @ExceptionHandler(MuitasTentativasException.class)
    public ResponseEntity<DadosErro> tratarMuitasTentativas(MuitasTentativasException e) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header("Retry-After", String.valueOf(e.getSegundosParaTentarDeNovo()))
                .body(new DadosErro(new Erro("muitas_tentativas", e.getMessage(), null)));
    }

    @ExceptionHandler(ChaveApiInvalidaException.class)
    public ResponseEntity<DadosErro> tratarChaveApi(ChaveApiInvalidaException e) {
        return erro(HttpStatus.UNAUTHORIZED, "chave_api_invalida", e.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<DadosErro> tratarMetodo(HttpRequestMethodNotSupportedException e) {
        return erro(HttpStatus.METHOD_NOT_ALLOWED, "metodo_nao_suportado", "Método " + e.getMethod() + " não suportado.");
    }

    @ExceptionHandler(ServicoExternoException.class)
    public ResponseEntity<DadosErro> tratarServicoExterno(ServicoExternoException e) {
        log.warn("Serviço externo indisponível: {}", e.getMessage());
        return erro(HttpStatus.BAD_GATEWAY, "servico_externo_indisponivel", e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<DadosErro> tratarErroInterno(Exception e) {
        // Exceções do Spring MVC já trazem o status certo (415 Content-Type, 406 Accept, ...): continuam 4xx.
        if (e instanceof ErrorResponse erroSpring && erroSpring.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(erroSpring.getStatusCode())
                    .body(new DadosErro(new Erro("requisicao_invalida", e.getMessage(), null)));
        }
        log.error("Erro inesperado", e);
        return erro(HttpStatus.INTERNAL_SERVER_ERROR, "erro_interno", "Erro interno no servidor.");
    }

    private ResponseEntity<DadosErro> erro(HttpStatus status, String codigo, String mensagem) {
        return ResponseEntity.status(status).body(new DadosErro(new Erro(codigo, mensagem, null)));
    }

    public record DadosErro(Erro erro) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Erro(String codigo, String mensagem, List<DadosCampo> campos) {
    }

    public record DadosCampo(String campo, String mensagem) {
        public DadosCampo(FieldError erro) {
            this(snakeCase(erro.getField()), erro.getDefaultMessage());
        }

        /** nivelCm -> nivel_cm, previsoes[0].localId -> previsoes[0].local_id (nomes iguais aos do JSON). */
        private static String snakeCase(String campo) {
            return campo.replaceAll("([a-z0-9])([A-Z])", "$1_$2").toLowerCase();
        }
    }
}
