package chatmulti;
/* Pacotes usados para montar a classe servidor. Nada mais é do que códigos
   já montados que utilizamos para diminuir o nosso, assim poupando de fazer 
   toda uma classe nova dentro desta.(Cada pacote será explicado 
   durante o código para que a linha de raciocínio seja melhor)*/
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/* A classe Servidor,java já é cetada para ser uma thread, assim adotando todos
   os comportamentos e propriedades da classe que é executar tarefas paralelas
   simultaneamente.*/
public class Servidor extends Thread {

// criando uma lista de clientes de tipo BufferedWriter para adicionar na conversa
    private static ArrayList<BufferedWriter> clientes;
// Aqui temos as declarações dos atributos estáticos para serem executados a necessidade criar objetos 
    private static ServerSocket server;
// Aqui temos as declarações dos atributos estáticos para serem executados a necessidade criar objetos 

   // atributo nome do tipo string no modo privado acessivel a classe
    private String nome;
   
// atributo con do tipo Socket 
    private Socket con;
// atributo in do tipo InputStream 
    private InputStream in;
// atributo inr do tipo InputStreamReader 
    private InputStreamReader inr;
// atributo bfr do tipo BufferedReader
    private BufferedReader bfr;

    /*Aqui temos o método construtor que recebe o objeto socket como parâmetro
      que cria outro objeto tido BufferedReader, que aponta para stream do 
      cliente.*/
    public Servidor(Socket con) {
        this.con = con;
        /*código que inclui comandos/invocações de métodos que podem gerar uma 
      situação de exceção.*/
        try {
            /* getInputStream nos retorna exatamente o que o cliente está enviando
       “do outro lado”. O conceito de Stream é um bloco genérico 
       para algum tipo de dados, podendo ele ser texto, vídeo, imagem e etc, 
       realmente não importa.*/
            in = con.getInputStream();
            /* A função do InputStreamReader é servir como um adaptador entre as duas 
       classes - lê bytes de um lado, converte em caracteres do outro, através 
       do uso de uma codificação de caracteres. Ou seja, ele é um Reader que 
       recebe um InputStream na construção, consumindo dados desse stream e 
      apresentando-os como caracteres para o consumidor.*/
            inr = new InputStreamReader(in);
            /* BufferedReader : Lê os caracteres um a um agregando-os em um buffer até 
      encontrar o caráter de fim de linha. Então esse conjunto de caracteres 
      pode ser tranforamdo em uma string e retornada como resultado desee 
      método).*/
            bfr = new BufferedReader(inr);
            /* IOException, que indica a ocorrência de algum tipo de erro em operações 
          de entrada e saída.*/
        } catch (IOException e) {
            /* printStackTrace método em Java é uma ferramenta usada para manusear 
       exceções e erros.*/
            e.printStackTrace();
        }
    }

    /*O metódo "run" é acionado e alocado em uma Thread e também verifica se existe
      uma nova mensagem do cliente que conectou, e caso exista, ele levará para
      o proximo metódo.*/
    public void run() {

        try {

            String msg;
            /*OutputStream é capaz de enviar dados a um determinado Stream, 
              ao contrário do InputStream que faz a leitura do mesmo, e já usamos
              getOutputStream() que nos retorna uma instância de OutputStream onde 
              podemos escrever o que quisermos e o servidor irá receber através do 
              InputStream.*/
            OutputStream ou = this.con.getOutputStream();
            /*Cria-se uma instancia writer para gravar uma string especifica no 
              fluxo e a classe OutputStreamWriter serve para converter caracteres
              em bytes.*/
            Writer ouw = new OutputStreamWriter(ou);
            /*O BufferedWriter envia os dados para a saída desejada imediatamente
              ao invês de ir um por vez.*/
            BufferedWriter bfw = new BufferedWriter(ouw);
           //adiciona na lista cliente o buffer, o que foi escrito 
            clientes.add(bfw);
            nome = msg = bfr.readLine();
            
           // enquanto diferente de sair e diferente de null
           //as mensagens são enviadas como parametro pelo metodo senToAll
           //sendo printado no console
            while (!"Sair".equalsIgnoreCase(msg) && msg != null) {
                msg = bfr.readLine();
               //se não clicou em sair envia essas informações digitadas passadas como parametro no metodo sendToAll() 
               sendToAll(bfw, msg);
                System.out.println(msg);
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
    }
    /*Metódo que faz a mensagem enviada pelo cliente1 ser mandada para o servidor
      onde os outros clientes leem a copia.*/
    public void sendToAll(BufferedWriter bwSaida, String msg) throws IOException {
        BufferedWriter bwS;
        
        //for percorrendo cada cliente de tipo BufferedWriter castiando cada para variavel bwS
        for (BufferedWriter bw : clientes) {
           //
            bwS = (BufferedWriter) bw;
           //se não foi solicitado a saida
           //printe a mensagem na tela e depois limpe o buffer
            if (!(bwSaida == bwS)) {
                // É assim que ficara na tela
                bw.write(nome + " -> " + msg + "\r\n");
                bw.flush();
            }
        }
    }

    /*Metódo principal que iniciara o servidor. Fará a configuração do servidor 
      socket e sua respectiva porta*/
    public static void main(String[] args) {

        try {
            //Cria os objetos necessário para instânciar o servidor
            
            /*JLabels são rótulos que podemos exibir em nossos frames.
              São elementos estáticos, não sendo usado para interagir com o 
              usuário*/
            JLabel lblMessage = new JLabel("Porta do Servidor:");
            /*representa um campo de texto onde o usuário pode informar um texto 
              em uma linha, linha essa que será a senha para entrar na porta*/
            JTextField txtPorta = new JTextField("12345");
            /*Objeto que recebe o texto*/
            Object[] texts = {lblMessage, txtPorta};
            /*JOptionPane possibilita a criação de uma caixa de dialogo padrão 
            que ou solicita um valor para o usuário ou retorna uma informação.*/
            JOptionPane.showMessageDialog(null, texts);
           //colocando no objeto server apos passar a porta digitada e parciada de string para integer
            server = new ServerSocket(Integer.parseInt(txtPorta.getText()));
            clientes = new ArrayList<BufferedWriter>();
            //mensagem para o cliente apresentando servidor ativo em porta xxxx  
           JOptionPane.showMessageDialog(null, "Servidor ativo na porta: "
                    + txtPorta.getText());

            /*Condicional de quando for verdadeira ele irá startar o servidor
              e escrevera as mensagens de aguardo, e quando o cliente conectar
              ira aparecer uma mensagem informando a conexão*/
            while (true) {
                System.out.println("Aguardando conexão...");
               //passando para variavel do tipo Socket a conexão feita pelo metodo accept()
                Socket con = server.accept();
                System.out.println("Cliente conectado...");
               //criando uma thead para cada conexão, cada cliente recebe o t.start(), isso é, se conecta
                Thread t = new Servidor(con);
                t.start();
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }// Fim do método main
} //Fim da classe

