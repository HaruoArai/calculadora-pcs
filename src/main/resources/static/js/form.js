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
        if (!valor.includes(',')) {

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

    // ---------- Bloqueia emendas conforme data final ----------
    const dataAtualizacao = document.getElementById('dataAtualizacao');
    const emendaEc136 = document.getElementById('emendaEc136');

    function atualizarEmendas() {

        if (!tipoCorrecao || !emendaEc113 || !emendaEc136 || !emendaNenhuma || !dataAtualizacao) {
            return;
        }

        const data = converterDataBR(dataAtualizacao.value);
        const marcoEc113 = new Date(2021, 11, 9); // 09/12/2021

        const dataAnteriorEmenda =
            data && data < marcoEc113;

        const correcaoSelic =
            tipoCorrecao.value === 'SELIC';

        function configurarEmenda(emenda, bloquear, mensagem) {
            const chip = emenda.closest('.opcao-chip');

            if (bloquear) {
                if (emenda.checked) {
                    emendaNenhuma.checked = true;
                }

                emenda.disabled = true;
                chip.classList.add('opcao-desativada');
                chip.setAttribute('title', mensagem);

            } else {
                emenda.disabled = false;
                chip.classList.remove('opcao-desativada');
                chip.removeAttribute('title');
            }
        }

        configurarEmenda(
            emendaEc113,
            dataAnteriorEmenda || correcaoSelic,
            correcaoSelic
                ? 'A EC 113/2021 não pode ser combinada com a correção SELIC.'
                : 'Regra constitucional disponível apenas para atualizações a partir de 09/12/2021.'
        );

        configurarEmenda(
            emendaEc136,
            dataAnteriorEmenda,
            'Regra constitucional disponível apenas para atualizações a partir de 09/12/2021.'
        );
    }

    function converterDataBR(dataTexto) {
        const partes = dataTexto.split('/');

        if (partes.length !== 3) {
            return null;
        }

        const dia = parseInt(partes[0], 10);
        const mes = parseInt(partes[1], 10) - 1;
        const ano = parseInt(partes[2], 10);

        if (isNaN(dia) || isNaN(mes) || isNaN(ano)) {
            return null;
        }

        return new Date(ano, mes, dia);
    }

    tipoCorrecao.addEventListener('change', atualizarEmendas);

    if (dataAtualizacao) {
        dataAtualizacao.addEventListener('input', atualizarEmendas);
        dataAtualizacao.addEventListener('change', atualizarEmendas);
    }

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

    function criarLinhaParcela(dataSugerida = '', valorSugerido = '0,00') {
        const index = parcelasContainer.querySelectorAll('.linha-parcela').length;

        const linha = document.createElement('div');
        linha.className = 'linha-parcela grid grid-3';
        linha.dataset.index = index;

        linha.innerHTML = `
            <div class="campo">
                <label class="form-label" style="visibility:hidden">
                    Data da Parcela
                </label>
                <input type="text"
                       class="form-control mask-data data-parcela"
                       placeholder="dd/mm/aaaa"
                       name="parcelas[${index}].dataParcela"
                       id="parcelas${index}.dataParcela"
                       value="${dataSugerida}">
            </div>
        
            <div class="campo">
                <label class="form-label" style="visibility:hidden">
                    Valor da Parcela (R$)
                </label>
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
                <label class="form-label" style="visibility:hidden">
                    &nbsp;
                </label>
                <button type="button"
                        class="btn btn-remover btn-remover-parcela"
                        title="Remover parcela">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round">
                        <polyline points="3 6 5 6 21 6"/>
                        <path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6"/>
                        <path d="M10 11v6"/>
                        <path d="M14 11v6"/>
                        <path d="M9 6V4h6v2"/>
                    </svg>
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

            criarLinhaParcela(novaData, '0,00');
            reindexarParcelas();
        });

        parcelasContainer.addEventListener('click', (e) => {
            const botao = e.target.closest('.btn-remover-parcela');

            if (!botao) return;

            const linhas = parcelasContainer.querySelectorAll('.linha-parcela');

            if (linhas.length <= 1) return;

            botao.closest('.linha-parcela').remove();
            reindexarParcelas();
        });
    }
    // ---------- Bootstrap Tooltips ----------
    // Inicializa todos os tooltips da página
    document.querySelectorAll('[data-bs-toggle="tooltip"]').forEach(el => {
        new bootstrap.Tooltip(el, { boundary: 'window' });
    });

    // ---------- Bloqueia citação e regra quando for SEM_JUROS ----------
    const tipoJuros = document.getElementById('tipoJuros');
    const dataCitacao = document.getElementById('dataCitacao');
    const blocoRegraJuros = document.getElementById('blocoRegraJuros');

    function atualizarCamposJuros() {
        if (!tipoJuros || !dataCitacao || !blocoRegraJuros) {
            return;
        }

        const semJuros = tipoJuros.value === 'SEM_JUROS';

        dataCitacao.disabled = semJuros;

        if (semJuros) {
            dataCitacao.value = '';
            blocoRegraJuros.classList.add('opcao-desativada');

            blocoRegraJuros
                .querySelectorAll('input')
                .forEach(input => input.disabled = true);
        } else {
            blocoRegraJuros.classList.remove('opcao-desativada');

            blocoRegraJuros
                .querySelectorAll('input')
                .forEach(input => input.disabled = false);

            const algumMarcado = blocoRegraJuros.querySelector('input:checked');

            if (!algumMarcado) {
                const padrao = blocoRegraJuros.querySelector('input[value="ACOMPANHA_PARCELA"]');
                if (padrao) padrao.checked = true;
            }
        }
    }

    if (tipoJuros) {
        tipoJuros.addEventListener('change', atualizarCamposJuros);
        atualizarCamposJuros();
    }
});