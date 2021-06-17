package com.mygdx.game.myflappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class MyGdxGame extends ApplicationAdapter {

    // Construção

    private SpriteBatch batch;
    private Texture[] passaros; // Array que tem as sprites do passarinho
    private Texture fundo; // Imagem do background
    private Texture canoInferior;// Imagem do cano inferior
    private Texture canoSuperior;// Imagem do cano superior
    private Texture gameOver; // Imagem do Game Over

    private int pontuacaoMaxima = 0; // Aqui o int faz seu papel guardando a pontuação maxima obtida
    private int pontos = 0; // Int que indica os pontos na partida
    private int estadoJogo = 0;// Alteração dos estados do jogo

    // Tela

    private float larguraDispositivo; //  O float está recebendo a largura do dispositivo
    private float alturaDispositivo; // O float está recebendo a altura do dispositivo

    private float posicaoCanoHorizontal = 0;
    private float posicaoCanoVertical;
    private float espaçoEntreCanos; // Float que está calculando o espaço entre os canos

    // Randomização dos canos na cena

    private Random random;

    private float variacao = 0; // Variação da altura para a animação
    private float gravidade = 0;// Float que da a gravidade para que o passarinho possa cair
    private float posicaoHorizontalPassaro = 0; // Posição horizontal do passarinho
    private float posicaoInicialVerticalPassaro = 0; // Posição inicial vertical do passarinho

    // Variaveis dos textos de pontuação, reinicio e da melnor pontuação adiquirida

    BitmapFont textoPontuacao;
    BitmapFont textoReiniciar;
    BitmapFont textoMelhorPontuacao;

    // Variaveis dos efeitos sonoros do jogo

    Sound somColisao;
    Sound somVoar;
    Sound somPontos;

    // Bool que faz a verificação se o personagem passou pelo cano

    private boolean passouCano = false;

    //  Criação dos colisores do jogo e também a renderização

    private ShapeRenderer shapeRenderer;
    private Circle circulopassaro;
    private Rectangle retanguloCanoSuperior;
    private Rectangle retanguloCanoInferior;

    Preferences preferencias;

    // Metodo instanciando os objetos que estão na tela

    @Override
    public void create () {
        inicializaImagens();
        inicializaTela();
    }

    // Metodo que consiste em imprimir a interface do layout da aplicação

    @Override
    public void render () {
        verificarEstadoJogo();
        validarPontos();
        desenhaImagens();
        detectarColisao();
    }


    private void inicializaTela() {
        batch = new SpriteBatch();

        random = new Random();

        larguraDispositivo = Gdx.graphics.getWidth();// Coletando a largura do dispositivo
        alturaDispositivo = Gdx.graphics.getHeight();// Coletando a altura do dispositivo
        posicaoInicialVerticalPassaro = alturaDispositivo/2; // Posicionando o passarinho no meio da tela
        posicaoCanoHorizontal = larguraDispositivo; // Posicionando o cano no dispositivo
        espaçoEntreCanos = 350;// Definindo o espaçamento entre os canos


        // Renderização dos textos, com cores e scale diferentes

        textoPontuacao = new BitmapFont();
        textoPontuacao.setColor( Color.WHITE);
        textoPontuacao.getData().setScale(10);

        textoMelhorPontuacao = new BitmapFont();
        textoMelhorPontuacao.setColor(Color.BLACK);
        textoMelhorPontuacao.getData().setScale(2);

        textoReiniciar = new BitmapFont();
        textoReiniciar.setColor(Color.RED);
        textoReiniciar.getData().setScale(2);

        // Criação dos colliders e inicialização

        shapeRenderer = new ShapeRenderer();
        circulopassaro = new Circle();
        retanguloCanoSuperior = new Rectangle();
        retanguloCanoInferior = new Rectangle();

        // Renderização dos efeitos sonoros

        somVoar = Gdx.audio.newSound(Gdx.files.internal("som_asa.wav"));
        somColisao = Gdx.audio.newSound(Gdx.files.internal("som_batida.wav"));
        somPontos = Gdx.audio.newSound(Gdx.files.internal("som_pontos.wav"));

        // Salvamento das preferencias e pontuação maxima atingida pelo jogador

        preferencias = Gdx.app.getPreferences("flappyBird");
        pontuacaoMaxima = preferencias.getInteger("pontuacaoMaxima", 0);
    }

    private void inicializaImagens() {

        // Definição no elemento 0, 1, 2. Quais serão as imagem do passarrinho

        passaros = new Texture[3];
        passaros[0] = new Texture("passaro1.png");
        passaros[1] = new Texture("passaro2.png");
        passaros[2] = new Texture("passaro3.png");

        // Instanciando as imagens do cano inferior, superior, background e game over

        fundo = new Texture("fundo.png" );
        canoSuperior = new Texture("cano_topo_maior.png");
        canoInferior = new Texture("cano_baixo_maior.png");
        gameOver = new Texture("game_over.png");
    }

    private void detectarColisao(){

        // Criando os colliders

        circulopassaro.set(50 + passaros[0].getWidth() / 2, posicaoInicialVerticalPassaro + passaros[0].getHeight() / 2, passaros[0].getWidth() / 2);

        retanguloCanoSuperior.set(posicaoCanoHorizontal, alturaDispositivo / 2 + espaçoEntreCanos/ 2 + posicaoCanoVertical, canoSuperior.getWidth(), canoSuperior.getHeight() );
        retanguloCanoInferior.set(posicaoCanoHorizontal, alturaDispositivo / 2 - canoInferior.getHeight() - espaçoEntreCanos / 2 + posicaoCanoVertical, canoInferior.getWidth(), canoInferior.getHeight());

        // Bool que tem a função de detectar as colisões

        boolean colisaoCanoSuperior = Intersector.overlaps(circulopassaro, retanguloCanoSuperior);
        boolean colisaoCanoInferior = Intersector.overlaps(circulopassaro, retanguloCanoInferior);

        // Se o passarinho collidir ele vai avisar

        if (colisaoCanoInferior || colisaoCanoSuperior)
        {
            if(estadoJogo ==1){
                somColisao.play();
                estadoJogo = 2;
            }
        }

    }
    private void verificarEstadoJogo() {

        boolean toqueTela = Gdx.input.justTouched(); //verifica se a tela foi tocada

        if (estadoJogo == 0) { // se o estado do jogo for 0, o jogo ainda esta no estado inicial e nao começou ainda
            if (Gdx.input.justTouched()) //se a tela é tocada, o passarinho vai pra cima e faz o som de voo
            {
                gravidade = -20;
                estadoJogo = 1;
                somVoar.play();
            }

        } else if (estadoJogo == 1) { // O jogo ira começar quando o estado for igual a 1

            if (Gdx.input.justTouched()) { // Quando a tela for tocada, o passarinho vai se movimentar e fazer o som de voo
                gravidade = -20;
                somVoar.play();
            }

            posicaoCanoHorizontal -= Gdx.graphics.getDeltaTime() * 200;// Os canos virao em direção ao jogador

            if (posicaoCanoHorizontal < -canoInferior.getWidth()) { // Coleta a largura da tela para obrigar ela avançar quando terminar
                posicaoCanoHorizontal = larguraDispositivo;
                posicaoCanoHorizontal = random.nextInt( 900 ) - 50;// Deixa ramdomizado o espaçamento
                passouCano = false; // Definição como falsa booleana
            }

            if (posicaoInicialVerticalPassaro > 0 || toqueTela)// Quando tocar na tela ira diminuir a gravidade do passarinho
                posicaoInicialVerticalPassaro = posicaoInicialVerticalPassaro - gravidade;
            gravidade++;

        } else if (estadoJogo == 2) { // Quando o estado do jogo for 2, significa que o player perdeu

            // Se a pontuação adiquirida pelo player for maior que a pontuação naxuna feita em outras tentativas, essa pontuação maxima virara uma nova pontuação

            if (pontos > pontuacaoMaxima) {
                pontuacaoMaxima = pontos;
                preferencias.putInteger( "pontuacaoMaxima", pontuacaoMaxima );// Salvando a pontuação
            }
            posicaoHorizontalPassaro -= Gdx.graphics.getDeltaTime() * 500;//quando o passaro colide ele volta para tras

            // Se o player tocar na tela o estado em que se encontra o jogo voltara para o inicial, os pontos pra zero, a gravidade ficara zerada

            if (toqueTela) {
                estadoJogo = 0;
                pontos = 0;
                gravidade = 0;
                posicaoHorizontalPassaro = 0;// Reseta a posição em que o passarinho se encontra
                posicaoInicialVerticalPassaro = alturaDispositivo / 2;// Passarinho instanciado no ponto inicial
                posicaoCanoHorizontal = larguraDispositivo; // Definição das posições dos canos
            }
        }
    }
    private void desenhaImagens() {

        // Começando a execução

        batch.begin();

        batch.draw(fundo,0,0,larguraDispositivo,alturaDispositivo);// Instanciando o fundo, utilizando os parametros  que foi cariado como altura e largura
        batch.draw(passaros[(int) variacao],50 + posicaoHorizontalPassaro, posicaoInicialVerticalPassaro);// Instanciando o passarinho

        // Instanciando os canos na tela com espaços entre eles

        batch.draw( canoInferior, posicaoCanoHorizontal , alturaDispositivo/2 - canoInferior.getHeight() - espaçoEntreCanos/2 + posicaoCanoVertical);
        batch.draw( canoSuperior, posicaoCanoHorizontal ,alturaDispositivo/2 + espaçoEntreCanos/2 + posicaoCanoVertical);

        textoPontuacao.draw( batch, String.valueOf( pontos ),larguraDispositivo /2, alturaDispositivo - 100 );// Pontuação é desenhada no topo da tela

        // Quando o estado for 2 é game over, também instanciando as frases de game over, melhor pontuação e reinicio da partida

        if(estadoJogo == 2)
        {
            batch.draw(gameOver, larguraDispositivo / 2 +200 - gameOver.getWidth(), alturaDispositivo / 2);
            textoReiniciar.draw(batch, "Toque na tela para reiniciar", larguraDispositivo / 2 - 250, alturaDispositivo /2 - gameOver.getHeight() / 2);
            textoMelhorPontuacao.draw(batch, "A melhor pontuação foi: " + pontuacaoMaxima +" Pontos adiquiridos", larguraDispositivo /2 - 250, alturaDispositivo /2 - gameOver.getHeight() * 2);
        }

        // A execução irá terminar

        batch.end();
    }

    private void validarPontos() {

        if (posicaoCanoHorizontal < 50 - passaros[0].getWidth())
        {
            // Quando o passarinho passar por um cano somara um ponto

            if (!passouCano){
                pontos++;
                passouCano = true;
                somPontos.play();
            }
        }
        variacao += Gdx.graphics.getDeltaTime() * 10; // Associação os graficos do gdx com a variação

        if(variacao > 3)// Alteração da variação para que a animação faça a mudança
            variacao = 0;
    }

    // O papel desse metodo é retornar os dados a aplicação

    @Override
    public void dispose () {

    }
}
