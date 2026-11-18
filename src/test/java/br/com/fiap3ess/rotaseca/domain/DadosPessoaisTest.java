package br.com.fiap3ess.rotaseca.domain;

import br.com.fiap3ess.rotaseca.shared.util.DadosPessoais;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class DadosPessoaisTest {
    @ParameterizedTest(name = "remove: {0}")
    @ValueSource(strings = {
            "me liga 11 98765-4321",
            "tel (11) 3456-7890",
            "whats +55 11 987654321",
            "CPF 123.456.789-09",
            "cpf 12345678909",
            "placa ABC1D23",
            "placa abc-1234",
            "email joao.silva+teste@email.com.br"
    })
    @DisplayName("Dado pessoal é trocado por [removido]")
    void remove(String texto) {
        DadosPessoais.Resultado r = DadosPessoais.remover(texto);
        assertThat(r.removeu()).isTrue();
        assertThat(r.texto()).contains(DadosPessoais.MARCADOR).doesNotContainPattern("\\d{4}");
    }

    @ParameterizedTest(name = "mantém: {0}")
    @ValueSource(strings = {
            "Água na altura do joelho às 18:48",
            "Av. das Nações Unidas, 12345",
            "nível 22 cm, carro parado no meio",
            "CEP 04693-130"
    })
    @DisplayName("Texto comum não é alterado")
    void mantem(String texto) {
        DadosPessoais.Resultado r = DadosPessoais.remover(texto);
        assertThat(r.removeu()).isFalse();
        assertThat(r.texto()).isEqualTo(texto);
    }
}
