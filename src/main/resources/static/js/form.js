// ============================================
// Máscaras e comportamento do formulário
// ============================================

document.addEventListener('DOMContentLoaded', () => {

    function aplicarMascaraData(input) {
        input.addEventListener('input', (e) => {
            let valor = e.target.value.replace(/\D/g, '').slice(0, 8);

            if (valor.length > 4) {
                valor = valor.replace(/(\d{2})(\d{2})(\d{0,4})/, '$1/$2/$3');
            } else if (valor.length > 2) {
                valor = valor.replace(/(\d{2})(\d{0,2})/, '$1/$2');
            }

            e.target.value = valor;
        });
    }

    function aplicarMascaraMoeda(input) {
        input.addEventListener('input', (e) => {
            let valorOriginal = e.target.value;

            const negativo = valorOriginal.includes('-');

            let valor = valorOriginal.replace(/\D/g, '');

            if (valor === '') {
                e.target.value = negativo ? '-' : '';
                return;
            }

            valor = valor.padStart(3, '0');

            const centavos = valor.slice(-2);
            let inteiro = valor.slice(0, -2).replace(/^0+(?=\d)/, '');

            inteiro = inteiro.replace(/\B(?=(\d{3})+(?!\d))/g, '.');

            e.target.value = `${negativo ? '-' : ''}${inteiro},${centavos}`;
        });
    }

    function formatarMoedaInicial(input) {
        if (!input.value) {
            return;
        }

        let valor = input.value.trim();

        // Se vier do Spring como 5000.00
        if (valor.includes('.') && !valor.includes(',')) {
            const numero = Number(valor);

            if (!isNaN(numero)) {
                input.value = numero.toLocaleString('pt-BR', {
                    minimumFractionDigits: 2,
                    maximumFractionDigits: 2
                });
            }
        }
    }

    // ---------- Máscara de data dd/mm/aaaa ----------
    document.querySelectorAll('.mask-data').forEach(aplicarMascaraData);

    // ---------- Máscara de moeda (R$ 1.234,56) ----------
    document.querySelectorAll('.mask-moeda').forEach(input => {
        formatarMoedaInicial(input);
        aplicarMascaraMoeda(input);
    });

    // ---------- Conversão antes do envio (moeda BR -> formato aceito pelo Spring) ----------
    const formCalculo = document.getElementById('form-calculo');

    if (formCalculo) {
        formCalculo.addEventListener('submit', (e) => {
            formCalculo.querySelectorAll('.mask-moeda').forEach(campoValor => {
                if (campoValor && campoValor.value) {
                    campoValor.value = campoValor.value
                        .replace(/\./g, '')
                        .replace(',', '.');
                }
            });

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

    // ---------- Parcelas dinâmicas ----------
    const parcelasContainer = document.getElementById('parcelas-container');
    const btnAdicionarParcela = document.getElementById('btn-adicionar-parcela');

    function somarUmMes(dataTexto) {
        const partes = dataTexto.split('/');

        if (partes.length !== 3) {
            return '';
        }

        const dia = parseInt(partes[0], 10);
        const mes = parseInt(partes[1], 10) - 1;
        const ano = parseInt(partes[2], 10);

        if (isNaN(dia) || isNaN(mes) || isNaN(ano)) {
            return '';
        }

        const data = new Date(ano, mes, dia);
        data.setMonth(data.getMonth() + 1);

        const novoDia = String(data.getDate()).padStart(2, '0');
        const novoMes = String(data.getMonth() + 1).padStart(2, '0');
        const novoAno = data.getFullYear();

        return `${novoDia}/${novoMes}/${novoAno}`;
    }

    function reindexarParcelas() {
        const linhas = parcelasContainer.querySelectorAll('.linha-parcela');

        linhas.forEach((linha, index) => {
            linha.dataset.index = index;

            const dataInput = linha.querySelector('.data-parcela');
            const valorInput = linha.querySelector('.valor-parcela');

            dataInput.name = `parcelas[${index}].dataParcela`;
            dataInput.id = `parcelas${index}.dataParcela`;

            valorInput.name = `parcelas[${index}].valorParcela`;
            valorInput.id = `parcelas${index}.valorParcela`;
        });
    }

    function criarLinhaParcela(dataSugerida = '', valorSugerido = '') {
        const index = parcelasContainer.querySelectorAll('.linha-parcela').length;

        const linha = document.createElement('div');
        linha.className = 'linha-parcela grid grid-3';
        linha.dataset.index = index;

        linha.innerHTML = `
        <div class="campo">
            <label class="form-label">Data da Parcela</label>
            <input type="text"
                   class="form-control mask-data data-parcela"
                   placeholder="dd/mm/aaaa"
                   name="parcelas[${index}].dataParcela"
                   id="parcelas${index}.dataParcela"
                   value="${dataSugerida}">
        </div>

        <div class="campo">
            <label class="form-label">Valor da Parcela (R$)</label>
            <div class="input-prefixo">
                <span>R$</span>
                <input type="text"
                       class="form-control mask-moeda valor-parcela"
                       name="parcelas[${index}].valorParcela"
                       id="parcelas${index}.valorParcela"
                       value="${valorSugerido}">
            </div>
        </div>

        <div class="campo campo-botao-parcela">
            <label class="form-label">&nbsp;</label>
            <button type="button" class="btn btn-secondary btn-remover-parcela">
                Excluir
            </button>
        </div>
    `;

        parcelasContainer.appendChild(linha);

        aplicarMascaraData(linha.querySelector('.mask-data'));
        aplicarMascaraMoeda(linha.querySelector('.mask-moeda'));
    }

    if (btnAdicionarParcela && parcelasContainer) {
        btnAdicionarParcela.addEventListener('click', () => {
            const linhas = parcelasContainer.querySelectorAll('.linha-parcela');
            const ultimaLinha = linhas[linhas.length - 1];
            const ultimaData = ultimaLinha.querySelector('.data-parcela').value;

            const novaData = somarUmMes(ultimaData);

            criarLinhaParcela(novaData, '');
            reindexarParcelas();
        });

        parcelasContainer.addEventListener('click', (e) => {
            if (e.target.classList.contains('btn-remover-parcela')) {
                const linhas = parcelasContainer.querySelectorAll('.linha-parcela');

                if (linhas.length <= 1) {
                    return;
                }

                e.target.closest('.linha-parcela').remove();
                reindexarParcelas();
            }
        });
    }

});