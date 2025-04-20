import model.Board;
import model.Space;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static util.BoardTemplate.BOARD_TEMPLATE;

public class Main {

  private final static Scanner scanner = new Scanner(System.in);

  private static Board board;

  private final static int BOARD_LIMIT = 9;
  private static boolean BoardTemplate;

  public static void main(String[] args) {

    final var positions = Stream.of(args).collect(Collectors.toMap(
            k -> k.split(";")[0],
            v -> v.split(";")[1]
    ));

    var option = -1;

    while (true) {
      System.out.println("Selecione uma das opções a seguir:");
      System.out.println("1 - Iniciar novo jogo");
      System.out.println("2 - Colocar novo numero");
      System.out.println("3 - Remover numero");
      System.out.println("4 - Visuzlizar jogo atual");
      System.out.println("5 - Verificar status do jogo:");
      System.out.println("6 - Limpar jogo");
      System.out.println("7 - Finalizar Jogo");
      System.out.println("8 - Sair");

      option = scanner.nextInt();

      switch (option) {
        case 1 -> startGame(positions);
        case 2 -> inputNumber();
        case 3 -> removeNumber();
        case 4 -> showCurrentGame();
        case 5 -> showGameStatus();
        case 6 -> clearGame();
        case 7 -> finishGames();
        case 8 -> System.exit(8);
        default -> System.out.println("Opção invalida ,Selecione uma das opções do menu:");
      }

    }

  }

  private static void finishGames() {
    if (isNull(board)) {
      System.out.println("O jogo ainda não foi iniciado");
      return;
    }
    if (board.gameIsFinished()) {
      System.out.println("Parabens voce concluiu o jogo!");
      showCurrentGame();
      board = null;
    } else if (board.hasErrors()) {
      System.out.println("Seu jogo contem erros, verifique seu jogo");
    }else {
      System.out.println("Você ainda precisa completar todos os espaços");
    }
  }

  private static void clearGame() {
    if (isNull(board)) {
      System.out.println("O jogo ainda não foi iniciado");
      return;
    }
    System.out.println("Tem certeza que deseja limpar o jogo atual?");
    var confirm = scanner.next();
    while (confirm.equalsIgnoreCase("sim") && confirm.equalsIgnoreCase("nao")) {
      System.out.println("informe sim ou nao");
      confirm = scanner.next();
    }
    if (confirm.equalsIgnoreCase("sim")) {
      board.reset();
    }

  }

  private static void showGameStatus() {
    if (isNull(board)) {
      System.out.println("O jogo ainda não foi iniciado");
      return;
    }
    System.out.printf("O jogo atualmente se encontra no status %s\n", board.getStatus().getLabel());
    if (board.hasErrors()) {
      System.out.println("O jogo contem erros");
    } else {
      System.out.println("O jogo não contem erros");
    }
  }

  private static void showCurrentGame() {
    if (isNull(board)) {
      System.out.println("O jogo ainda não foi iniciado");
      return;
    }
    var args = new Object[81];
    var argsPos = 0;
    for (int i = 0; i < BOARD_LIMIT; i++) {
      for (var col : board.getSpaces()) {
        args[argsPos++] = " " + ((isNull(col.get(i).getActual())) ? " " : col.get(i).getActual());
      }
    }
    System.out.println("Seu jogo esta assim: ");
    System.out.printf((BOARD_TEMPLATE) + "%n", args);

  }

  private static void removeNumber() {
    if (isNull(board)) {
      System.out.println("O jogo ainda não foi iniciado");
      return;
    }
    System.out.println("Informe a coluna que o numero sera removido");
    var col = runUntilGetInvalidNumber(0, 8);
    System.out.println("Informe a linha que o numero sera removido");
    var row = runUntilGetInvalidNumber(0, 8);
    if (!board.clearValue(col, row)) {
      System.out.printf("A posição [%s,%s] tem um valor fixo\n", col, row);
    }
  }

  private static void inputNumber() {
    if (isNull(board)) {
      System.out.println("O jogo ainda não foi iniciado");
      return;
    }
    System.out.println("Informe a coluna que o numero sera inserido");
    var col = runUntilGetInvalidNumber(0, 8);
    System.out.println("Informe a linha que o numero sera inserido");
    var row = runUntilGetInvalidNumber(0, 8);
    System.out.printf("Informe o numero que vai entrar na posição [%s,%s]\n", col, row);
    var value = runUntilGetInvalidNumber(1, 9);
    if (!board.changeValue(col, row, value)) {
      System.out.printf("A posição [%s,%s] tem um valor fixo\n", col, row);
    }
  }

  private static void startGame(Map<String, String> positions) {
    if (nonNull(board)) {
      System.out.println("O jogo já foi iniciado");
      return;
    }
    List<List<Space>> spaces = new ArrayList<>();
    for (int i = 0; i < BOARD_LIMIT; i++) {
      spaces.add(new ArrayList<>());
      for (int j = 0; j < BOARD_LIMIT; j++) {
        var positionConfig = positions.get("%s,%s".formatted(i, j));
        var expect = Integer.parseInt(positionConfig.split(",")[0]);
        var fixed = Boolean.parseBoolean(positionConfig.split(",")[1]);
        var currentSpace = new Space(expect, fixed);
        spaces.get(i).add(currentSpace);
      }
    }
    board = new Board(spaces);
    System.out.println("O jogo esta pronto para começar");
  }

  private static int runUntilGetInvalidNumber(final int min, final int max) {
    var current = scanner.nextInt();
    while (current < min || current > max) {
      System.out.printf("informe um numero entre %s e %s\n", min, max);
      current = scanner.nextInt();
    }
    return current;
  }

}

