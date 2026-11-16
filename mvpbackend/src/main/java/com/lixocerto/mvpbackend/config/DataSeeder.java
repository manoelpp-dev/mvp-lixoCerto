package com.lixocerto.mvpbackend.config;

import com.lixocerto.mvpbackend.model.CategoriaLixo;
import com.lixocerto.mvpbackend.model.InstrucaoDescarte;
import com.lixocerto.mvpbackend.model.Lixo;
import com.lixocerto.mvpbackend.model.PontoDescarte;
import com.lixocerto.mvpbackend.repositories.CategoriaLixoRepository;
import com.lixocerto.mvpbackend.repositories.InstrucaoDescarteRepository;
import com.lixocerto.mvpbackend.repositories.LixoRepository;
import com.lixocerto.mvpbackend.repositories.PontoDescarteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Carga inicial. Só roda com o banco vazio e pode ser desligada com app.seed.enabled=false.
 *
 * - Catálogo (categorias, instruções, lixos): dados levantados pelo grupo (os mesmos dos antigos
 *   scripts insert_*.sql), carregados via JPA. Os números passados a categoria(...) e instrucao(...)
 *   são os ids originais dos scripts, usados só para ligar um lixo à sua categoria e instrução.
 *   Complementos feitos depois: palavras-chave (busca por texto) e seco/úmido de cada lixo.
 * - Ecopontos: lidos de src/main/resources/ecopontos.csv.
 *
 * Por que via JPA e não por INSERT com id fixo: o SQL depende da ordem das colunas da tabela e não
 * avança a sequência do id, o que quebraria o POST de novos registros depois.
 */
@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DataSeeder implements CommandLineRunner {

    private final CategoriaLixoRepository categorias;
    private final InstrucaoDescarteRepository instrucoes;
    private final LixoRepository lixos;
    private final PontoDescarteRepository pontos;

    private final Map<Integer, CategoriaLixo> categoriasPorId = new HashMap<>();
    private final Map<Integer, InstrucaoDescarte> instrucoesPorId = new HashMap<>();

    public DataSeeder(CategoriaLixoRepository categorias, InstrucaoDescarteRepository instrucoes,
                      LixoRepository lixos, PontoDescarteRepository pontos) {
        this.categorias = categorias;
        this.instrucoes = instrucoes;
        this.lixos = lixos;
        this.pontos = pontos;
    }

    @Override
    @Transactional
    public void run(String... args) {
        carregarCatalogo();
        carregarEcopontos();
    }

    // ---------------- catálogo ----------------

    private void carregarCatalogo() {
        if (categorias.count() > 0) return;

        // categorias: (id original, descrição, grupo)
        categoria(1, "Resíduos de origem biológica, como restos de alimentos, cascas de frutas e legumes, e folhas", "Orgânico");
        categoria(2, "Materiais que podem ser transformados em novos produtos", "Reciclável");
        categoria(3, "Itens que apresentam riscos à saúde ou ao meio ambiente por conterem substâncias tóxicas", "Perigoso");
        categoria(4, "O que não pode mais ser reaproveitado ou reciclado e deve ir para os aterros sanitários", "Rejeito");

        // instruções de descarte: (id original, cor, instrução, sugestão de reciclagem)
        instrucao(1, "AZUL",
                "Descarte papéis e papelões limpos e secos, dobre caixas de papelão para reduzir o volume, retire restos de alimentos, plásticos e outros materiais das embalagens. Evite descartar papéis engordurados, molhados ou contaminados e sempre que possível retirar grampos e clipes",
                "Transformação em papel reciclado/ Reutilizar papelão como caixas organizadoras/ Montar com o papelão casinhas, carrinhos, pistas de rolamento/ Compostagem com papéis simples e sem química/ Cobrir com pedaços de papelão o solo do jardim para reter umidade e o crescimento de ervas daninhas");
        instrucao(2, "VERMELHO",
                "Esvazie completamente embalagens, retirando o excesso de resíduos de alimentos e líquidos. Se necessário, enxágue rapidamente para evitar mau cheiro e contaminação e deixe os materiais secos. Garrafas PET podem ser comprimidas para diminuir o volume",
                "Garrafas PET podem virar novas embalagens/ Plásticos rígidos podem ser transformados em baldes e vasos/ Sacolas plásticas em boas condições podem ser reutilizadas para guardar novos produtos/ Reutilizar na fabricação de móveis e materiais de construção");
        instrucao(3, "VERDE",
                "Esvazie garrafas e potes, retire tampas, separe materiais metálicos ou plásticos e se possível enxágue os recipientes. Embale cacos de vidro em papelão resistente ou recipiente rígido e sempre identifique claramente os materiais quebrados para evitar acidentes. Não misture espelhos, porcelanas, cristais e cerâmicas com vidro comum",
                "Fabricação de novas garrafas, potes, frascos e embalagens");
        instrucao(4, "AMARELO",
                "Esvazie latas e embalagens metálicas, retire restos de alimentos e líquidos e sempre quando necessário enxágue. Amasse latas de alumínio para reduzir o volume, se isso for seguro e não descarte objetos cortantes sem a proteção adequada",
                "Latas de alumínio ou de aço podem ser reutilizadas como organizadores (ex: porta-lápis)/ Pedaços de cobre ou aço podem virar puxadores para gavetas ou suportes para prateleiras");
        instrucao(5, "MARROM",
                "Separe restos de alimentos de materiais recicláveis, evite misturar plástico, vidro, metal e outros materiais não orgânicos. Armazene os resíduos em recipientes fechados para evitar odores e insetos e não descarte óleo de cozinha junto aos resíduos orgânicos",
                "Compostagem doméstica ou comunitária/ Produção de adubo orgânico e fertilizantes naturais");
        instrucao(6, "LARANJA",
                "Não descarte aparelhos eletrônicos no lixo doméstico ou nas lixeiras convencionais. Se possível, encaminhe computadores, celulares, televisores, teclados e outros equipamentos para pontos de coleta específicos de lixo eletrônico e se possível apague dados pessoais e restaure as configurações de fábrica de celulares e computadores antes do descarte. Retire pilhas e baterias removíveis e encaminhe-as separadamente. Não desmonte equipamentos sem conhecimento técnico",
                "Metais como cobre, alumínio, ouro e prata podem ser recuperados/ Componentes eletrônicos, plásticos e outros materiais podem ser reaproveitados/ Doação de equipamentos ainda funcionais");
        instrucao(7, "LARANJA",
                "Nunca descarte pilhas e baterias no lixo comum ou diretamente no meio ambiente, armazene-as em local seco, protegido do calor e fora do alcance de crianças e animais. Proteja os terminais de baterias de lítio contra curto-circuitos, seguindo as orientações do fabricante. Baterias automotivas devem ser devolvidas a revendedores ou estabelecimentos especializados.",
                null);
        instrucao(8, "PRETO",
                "Separe a madeira de outros tipos de resíduos e retire pregos, parafusos, dobradiças e peças metálicas quando possível. Desmonte móveis grandes para facilitar o transporte. Não descarte madeira em calçadas, terrenos baldios ou junto ao lixo doméstico e não queime madeira descartada, especialmente quando possuir tratamentos químicos",
                "Reaproveitamento na fabricação de móveis/ Produção de painéis de madeira reconstituída, como MDF e aglomerados, quando tecnicamente adequada/ Fabricação de objetos decorativos e artesanato/ Transformação de resíduos limpos em matéria-prima para novos produtos");
        instrucao(9, "CINZA",
                "Separe os rejeitos dos materiais recicláveis e orgânicos, descarte os resíduos em sacos resistentes e bem fechados. Evite misturar materiais perigosos, como pilhas, baterias, medicamentos e produtos químicos e não descarte resíduos em terrenos baldios, rios, bueiros ou vias públicas.",
                null);
        instrucao(10, "SEM COR ESPECÍFICA",
                "Aguarde o óleo esfriar completamente após a utilização, coe o óleo para retirar restos de alimentos e armazene em garrafas PET ou recipientes resistentes, bem fechados. Não misture óleo de cozinha com óleo de motor ou outros lubrificantes.",
                "Fabricação de sabão, detergentes e demais produtos de limpeza");

        // lixos: (nome, descrição, id da categoria, id da instrução, úmido?, palavras-chave)
        lixo("Papel e papelão",
                "Folhas de papel e jornais, revistas e folhetos, caixas de papelão, cadernos e envelopes",
                2, 1, false,
                "papel, papelão, caixa, caixa de papelão, jornal, revista, folheto, panfleto, caderno, envelope, folha de papel, sulfite, cartolina, livro");
        lixo("Plástico",
                "Garrafas PET, sacolas plásticas, potes e embalagens, copos descartáveis, canudos e tampas",
                2, 2, false,
                "plástico, pet, garrafa pet, garrafa, garrafinha, sacola, sacolinha, saco, saquinho, pote, embalagem, copo descartável, copo, canudo, tampa, refrigerante, frasco, detergente, shampoo, margarina, balde, bandeja");
        lixo("Vidro",
                "Garrafas, potes de conserva e de alimentos, frascos de perfume, cacos de vidro",
                2, 3, false,
                "vidro, garrafa de vidro, garrafa, pote de vidro, pote, conserva, frasco, perfume, caco, cacos de vidro, cerveja, garrafa de cerveja, long neck, vinho, copo de vidro");
        lixo("Metais",
                "Latas de alumínio, aço, tampas metálicas, panelas, fios, peças, sucatas",
                2, 4, false,
                "metal, metais, lata, latinha, alumínio, aço, tampa de metal, panela, fio, sucata, lata de refrigerante, lata de cerveja, lata de conserva, lata de atum, arame");
        lixo("Restos de alimentos/ Material Orgânico",
                "Restos de comida, cascas de frutas e verduras, borra de café, casca de ovos, folhas e galhos",
                1, 5, true,
                "comida, restos de comida, sobra, sobras, casca, cascas, fruta, verdura, legume, borra de café, café, ovo, casca de ovo, folha, galho, orgânico, alimento, comida estragada, erva-mate");
        lixo("Eletrônicos (E-lixo)",
                "Computadores, notebooks, celulares, teclados, mouses, monitores, cabos, carregadores",
                3, 6, false,
                "eletrônico, eletrônicos, celular, telefone, computador, notebook, teclado, mouse, monitor, tela, cabo, carregador, fone, fone de ouvido, tv, televisão, controle remoto, impressora, tablet, eletrodoméstico, aparelho");
        lixo("Pilhas e baterias",
                "Pilhas AA e AAA e baterias",
                3, 7, false,
                "pilha, pilhas, bateria, baterias, pilha aa, pilha aaa, bateria de celular, bateria de notebook, pilha recarregável, bateria de carro");
        lixo("Madeira",
                "Móveis velhos, pallets, tábuas, ripas, sobras de madeira, caixas",
                2, 8, false,
                "madeira, móvel, móveis, pallet, palete, tábua, ripa, caixote, compensado, mdf, armário, cadeira, mesa, guarda-roupa, sobra de madeira");
        lixo("Lixo não reciclável",
                "Papel higiênico usado, guardanapos sujos, fraldas, absorventes, fitas, etiquetas adesivas",
                4, 9, true,
                "rejeito, não reciclável, higiênico, guardanapo sujo, fralda, fraldas, absorvente, fita, etiqueta, adesivo, cotonete, algodão, esponja, bituca, cigarro, cabelo, curativo");
        lixo("Óleo de cozinha",
                "Óleo de cozinha comum utilizado para cozinhar",
                2, 10, true,
                "óleo, óleo de cozinha, óleo de fritura, óleo usado, gordura, gordura de fritura, azeite, óleo frito");
    }

    private void categoria(int idOriginal, String descricao, String tipoDeLixo) {
        CategoriaLixo c = new CategoriaLixo();
        c.setDescricao(descricao);
        c.setTipo_de_lixo(tipoDeLixo);
        categoriasPorId.put(idOriginal, categorias.save(c));
    }

    private void instrucao(int idOriginal, String cor, String instrucao, String sugestaoReciclagem) {
        InstrucaoDescarte i = new InstrucaoDescarte();
        i.setCorLixo(cor);
        i.setInstrucao(instrucao);
        i.setSugestaoReciclagem(sugestaoReciclagem);
        instrucoesPorId.put(idOriginal, instrucoes.save(i));
    }

    private void lixo(String nome, String descricao, int categoriaId, int instrucaoId,
                      Boolean umido, String palavrasChave) {
        Lixo l = new Lixo();
        l.setNome(nome);
        l.setDescricao(descricao);
        l.setCategoria(categoriasPorId.get(categoriaId));
        l.setInstrucaoDescarte(instrucoesPorId.get(instrucaoId));
        l.setUmido(umido);
        l.setPalavrasChave(palavrasChave);
        lixos.save(l);
    }

    // ---------------- ecopontos (CSV) ----------------

    private void carregarEcopontos() {
        if (pontos.count() > 0) return;

        ClassPathResource arquivo = new ClassPathResource("ecopontos.csv");
        if (!arquivo.exists()) return;

        try (BufferedReader leitor = new BufferedReader(
                new InputStreamReader(arquivo.getInputStream(), StandardCharsets.UTF_8))) {
            String linha;
            int numero = 0;
            while ((linha = leitor.readLine()) != null) {
                numero++;
                if (numero == 1 || linha.isBlank() || linha.startsWith("#")) continue; // cabeçalho, vazias e comentários
                pontos.save(lerPonto(linha, numero));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível ler ecopontos.csv", e);
        }
    }

    // colunas: nome;descricao;cidade;rua;num;cep;bairro;latitude;longitude;status  (status é opcional; padrão ATIVO)
    private PontoDescarte lerPonto(String linha, int numero) {
        String[] c = linha.split(";", -1);
        if (c.length < 9) {
            throw new IllegalStateException("ecopontos.csv, linha " + numero + ": esperado 9 ou 10 colunas separadas por ';'");
        }
        try {
            PontoDescarte p = new PontoDescarte();
            p.setNome(obrigatorio(c[0], "nome", numero));
            p.setDescricao(texto(c[1]));
            p.setCidade(obrigatorio(c[2], "cidade", numero));
            p.setRua(texto(c[3]));
            p.setNum(texto(c[4]) == null ? null : Integer.valueOf(texto(c[4])));
            p.setCep(texto(c[5]));
            p.setBairro(texto(c[6]));
            p.setLatitude(texto(c[7]) == null ? null : Double.valueOf(texto(c[7]).replace(',', '.')));
            p.setLongitude(texto(c[8]) == null ? null : Double.valueOf(texto(c[8]).replace(',', '.')));
            if (c.length > 9 && texto(c[9]) != null) {
                p.setStatus(texto(c[9]).toUpperCase());
            }
            return p;
        } catch (NumberFormatException e) {
            throw new IllegalStateException("ecopontos.csv, linha " + numero + ": número inválido (num, latitude ou longitude)", e);
        }
    }

    private String texto(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }

    private String obrigatorio(String valor, String campo, int numero) {
        String t = texto(valor);
        if (t == null) {
            throw new IllegalStateException("ecopontos.csv, linha " + numero + ": o campo '" + campo + "' é obrigatório");
        }
        return t;
    }
}
