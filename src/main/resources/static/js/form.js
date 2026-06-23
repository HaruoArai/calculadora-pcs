// ============================================
// Máscaras e comportamento do formulário
// ============================================

document.addEventListener('DOMContentLoaded', () => {

    // ---------- Máscara de data dd/mm/aaaa ----------
    document.querySelectorAll('.mask-data').forEach(input => {
        input.addEventListener('input', (e) => {
            let valor = e.target.value.replace(/\D/g, '').slice(0, 8);

            if (valor.length > 4) {
                valor = valor.replace(/(\d{2})(\d{2})(\d{0,4})/, '$1/$2/$3');
            } else if (valor.length > 2) {
                valor = valor.replace(/(\d{2})(\d{0,2})/, '$1/$2');
            }

            e.target.value = valor;
        });
    });

    // ---------- Máscara de moeda (R$ 1.234,56) ----------
    document.querySelectorAll('.mask-moeda').forEach(input => {
        input.addEventListener('input', (e) => {
            let valor = e.target.value.replace(/\D/g, '');

            if (valor === '') {
                e.target.value = '';
                return;
            }

            // Garante pelo menos 3 dígitos para ter centavos
            valor = valor.padStart(3, '0');

            const centavos = valor.slice(-2);
            let inteiro = valor.slice(0, -2).replace(/^0+(?=\d)/, '');

            // Adiciona separador de milhar
            inteiro = inteiro.replace(/\B(?=(\d{3})+(?!\d))/g, '.');

            e.target.value = `${inteiro},${centavos}`;
        });
    });

    // ---------- Conversão antes do envio (moeda BR -> formato aceito pelo Spring) ----------
    const formCalculo = document.getElementById('form-calculo');
    if (formCalculo) {
        formCalculo.addEventListener('submit', (e) => {
            const campoValor = formCalculo.querySelector('.mask-moeda');
            if (campoValor && campoValor.value) {
                // "1.234,56" -> "1234.56"
                const valorConvertido = campoValor.value
                    .replace(/\./g, '')
                    .replace(',', '.');
                campoValor.value = valorConvertido;
            }

            // Feedback visual de carregamento
            const btn = document.getElementById('btn-calcular');
            if (btn) {
                btn.querySelector('.btn-texto').textContent = 'Calculando...';
                btn.querySelector('.spinner').hidden = false;
                btn.disabled = true;
            }
        });
    }

    // ---------- Regra EC113 x SELIC ----------
    const tipoCorrecao = document.getElementById('tipoCorrecao');
    const emendaEc113 = document.getElementById('emendaEc113');
    const emendaNenhuma = document.getElementById('emendaNenhuma');

    function atualizarEmendas() {

        if (!tipoCorrecao || !emendaEc113 || !emendaNenhuma) {
            return;
        }

        if (tipoCorrecao.value === 'SELIC') {

            if (emendaEc113.checked) {
                emendaNenhuma.checked = true;
            }

            emendaEc113.disabled = true;
            emendaEc113.closest('.opcao-chip')
                .classList.add('opcao-desativada');

        } else {

            emendaEc113.disabled = false;
            emendaEc113.closest('.opcao-chip')
                .classList.remove('opcao-desativada');
        }
    }

    tipoCorrecao.addEventListener('change', atualizarEmendas);
    atualizarEmendas();

});