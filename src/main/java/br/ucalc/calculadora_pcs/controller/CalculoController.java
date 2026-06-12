package br.ucalc.calculadora_pcs.controller;

import br.ucalc.calculadora_pcs.dto.CalculoFormDTO;
import br.ucalc.calculadora_pcs.model.Calculo;
import br.ucalc.calculadora_pcs.model.ItemCalculo;
import br.ucalc.calculadora_pcs.model.Processo;
import br.ucalc.calculadora_pcs.model.enums.TipoCorrecao;
import br.ucalc.calculadora_pcs.model.enums.TipoJuros;
import br.ucalc.calculadora_pcs.repository.CalculoRepository;
import br.ucalc.calculadora_pcs.repository.ProcessoRepository;
import br.ucalc.calculadora_pcs.service.CalculoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/calculo")
public class CalculoController {

    private final ProcessoRepository processoRepository;
    private final CalculoRepository calculoRepository;
    private final CalculoService calculoService;

    public CalculoController(ProcessoRepository processoRepository,
                             CalculoRepository calculoRepository,
                             CalculoService calculoService) {
        this.processoRepository = processoRepository;
        this.calculoRepository = calculoRepository;
        this.calculoService = calculoService;
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("form", new CalculoFormDTO());
        model.addAttribute("tiposCorrecao", TipoCorrecao.values());
        model.addAttribute("tiposJuros", TipoJuros.values());
        return "calculo/form";
    }

    @PostMapping("/calcular")
    public String calcular(@ModelAttribute("form") @Valid CalculoFormDTO form,
                           BindingResult result,
                           Model model) {

        if (result.hasErrors()) {
            model.addAttribute("tiposCorrecao", TipoCorrecao.values());
            model.addAttribute("tiposJuros", TipoJuros.values());
            return "calculo/form";
        }

        // Monta e salva o Processo
        Processo processo = new Processo();
        processo.setAutor(form.getAutor());
        processo.setReu(form.getReu());
        processo.setAutos(form.getAutos());
        processo.setComarca(form.getComarca());
        processo.setVara(form.getVara());
        processo.setPgenet(form.getPgenet());
        processoRepository.save(processo);

        // Monta o Calculo
        Calculo calculo = new Calculo();
        calculo.setProcesso(processo);
        calculo.setDataInicioCorrecao(
                form.getDataInicioCorrecao());
        calculo.setDataFimCorrecao(
                form.getDataFimCorrecao());
        calculo.setDataInicioJuros(
                form.getDataInicioJuros());
        calculo.setDataFimJuros(
                form.getDataFimJuros());
        calculo.setCompetenciaInicial(
                form.getCompetenciaInicial());
        calculo.setCompetenciaFinal(
                form.getCompetenciaFinal());
        calculo.setTipoCorrecao(form.getTipoCorrecao());
        calculo.setTipoJuros(form.getTipoJuros());
        calculo.setValorDevidoInicial(form.getValorDevidoInicial());

        // Gera as linhas da tabela
        List<ItemCalculo> itens = calculoService.gerarTabela(calculo);
        itens.forEach(item -> item.setCalculo(calculo));
        calculo.setItens(itens);

        calculoRepository.save(calculo);

        return "redirect:/calculo/resultado/" + calculo.getId();
    }

    @GetMapping("/resultado/{id}")
    public String resultado(@PathVariable Long id, Model model) {
        Calculo calculo = calculoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cálculo não encontrado: " + id));
        model.addAttribute("calculo", calculo);
        return "calculo/resultado";
    }
}
