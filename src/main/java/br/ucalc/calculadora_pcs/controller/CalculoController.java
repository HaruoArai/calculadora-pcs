package br.ucalc.calculadora_pcs.controller;

import br.ucalc.calculadora_pcs.dto.CalculoFormDTO;
import br.ucalc.calculadora_pcs.dto.ParcelaFormDTO;
import br.ucalc.calculadora_pcs.model.Calculo;
import br.ucalc.calculadora_pcs.model.ItemCalculo;
import br.ucalc.calculadora_pcs.model.Processo;
import br.ucalc.calculadora_pcs.model.enums.TipoCorrecao;
import br.ucalc.calculadora_pcs.model.enums.TipoEmenda;
import br.ucalc.calculadora_pcs.model.enums.TipoJuros;
import br.ucalc.calculadora_pcs.model.ParcelaCalculo;
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
        CalculoFormDTO form = new CalculoFormDTO();
        form.getParcelas().add(new ParcelaFormDTO());
        model.addAttribute("form", form);
        model.addAttribute("tiposCorrecao", TipoCorrecao.values());
        model.addAttribute("tiposJuros", TipoJuros.values());
        model.addAttribute("tiposEmenda", TipoEmenda.values());
        return "calculo/form";
    }

    @PostMapping("/calcular")
    public String calcular(@ModelAttribute("form") @Valid CalculoFormDTO form,
                           BindingResult result,
                           Model model) {

        if (result.hasErrors()) {
            model.addAttribute("tiposCorrecao", TipoCorrecao.values());
            model.addAttribute("tiposJuros", TipoJuros.values());
            model.addAttribute("tiposEmenda", TipoEmenda.values());
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
        calculo.setDataAtualizacao(form.getDataAtualizacao());
        calculo.setDataCitacao(form.getDataCitacao());

        calculo.setTipoCorrecao(form.getTipoCorrecao());
        calculo.setTipoJuros(form.getTipoJuros());
        calculo.setTipoEmenda(form.getTipoEmenda());

        // Mantém a primeira parcela no cálculo apenas para referência
        calculo.setDataParcela(form.getParcelas().get(0).getDataParcela());
        calculo.setValorDevidoInicial(form.getParcelas().get(0).getValorParcela());

        for (ParcelaFormDTO parcelaDTO : form.getParcelas()) {
            ParcelaCalculo parcela = new ParcelaCalculo();
            parcela.setCalculo(calculo);
            parcela.setDataParcela(parcelaDTO.getDataParcela());
            parcela.setValorParcela(parcelaDTO.getValorParcela());

            calculo.getParcelas().add(parcela);
        }

        // Gera as linhas da tabela
        List<ItemCalculo> itens =
                calculoService.gerarTabela(
                        calculo,
                        form.getParcelas());
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

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Calculo calculo = calculoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cálculo não encontrado: " + id));

        CalculoFormDTO form = new CalculoFormDTO();

        form.setAutor(calculo.getProcesso().getAutor());
        form.setReu(calculo.getProcesso().getReu());
        form.setAutos(calculo.getProcesso().getAutos());
        form.setComarca(calculo.getProcesso().getComarca());
        form.setVara(calculo.getProcesso().getVara());
        form.setPgenet(calculo.getProcesso().getPgenet());

        form.setDataAtualizacao(calculo.getDataAtualizacao());
        form.setDataCitacao(calculo.getDataCitacao());
        form.setTipoCorrecao(calculo.getTipoCorrecao());
        form.setTipoJuros(calculo.getTipoJuros());
        form.setTipoEmenda(calculo.getTipoEmenda());

        form.getParcelas().clear();

        for (ParcelaCalculo parcela : calculo.getParcelas()) {
            ParcelaFormDTO parcelaDTO = new ParcelaFormDTO();
            parcelaDTO.setDataParcela(parcela.getDataParcela());
            parcelaDTO.setValorParcela(parcela.getValorParcela());

            form.getParcelas().add(parcelaDTO);
        }

        model.addAttribute("form", form);
        model.addAttribute("tiposCorrecao", TipoCorrecao.values());
        model.addAttribute("tiposJuros", TipoJuros.values());
        model.addAttribute("tiposEmenda", TipoEmenda.values());

        return "calculo/form";
    }
}
