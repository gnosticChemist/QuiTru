
  # Framework
  
  Para começar a modificar o código recomendo estudar a framework usada no projeto, libGDX. Para isso, recomendo estudar pelas fontes oficiais do site deles: https://libgdx.badlogicgames.com . Se possível desenvolva um jogo bem simples do zero, vai auxiliar bastante.
  No jogo todo se usou Stages para se adicionar os elementos visuais na tela, seguindo mais ou menos uma certa ordem:
    - Contruir o estilo do elemento;
    - Construir o elemento;
    - Definição do tamanho e posição do elemento;
    - Configurações especiais do elemento;
    - Adição de eventos ao elemento; 
  O unico módulo opcional usado  foi o FreeTypeFont para se gerar as fontes, e os assests padrões são na pasta /android/assets
  A classe CustomUtils e a MyScreen foram desenvolvidas para reduzir código e facilitar tudo.


  # Internet
  
  O jogo encontra outros jogadores com as classes da KryoNet, mas o server e client deles não conseguiu funcionar por algum motivo, então se usa sockets normais para isso.


  # Dados
 
  Os dados do jogo são salvos em json, que a libGDX gerencia perfeitamente.
