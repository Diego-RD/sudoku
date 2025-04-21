package ui.custom.screen;

import model.Space;
import service.BoardService;
import service.EventEnum;
import service.NotifierService;
import ui.custom.button.CheckGameStatusButton;
import ui.custom.button.FinishGameButton;
import ui.custom.button.ResetButton;
import ui.custom.frame.MainFrame;
import ui.custom.input.NumberText;
import ui.custom.painel.MainPainel;
import ui.custom.painel.SudokuSector;

import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.util.Map;

import static javax.swing.JOptionPane.QUESTION_MESSAGE;
import static javax.swing.JOptionPane.YES_NO_OPTION;
import static service.EventEnum.CLEAR_SPACE;

public class MainScreen {

  private final static Dimension dimension = new Dimension(600, 600);

  private final BoardService boardService;
  private final NotifierService notifierService;

  private JButton finishGameButton;
  private JButton checkGameStatusButton;
  private JButton resetButton;


  public MainScreen(Map<String, String> gameConfig) {
    this.boardService = new BoardService(gameConfig);
    this.notifierService = new NotifierService();
  }

  public void buildMainScreen() {
    JPanel mainPainel = new MainPainel(dimension);
    JFrame mainFrame = new MainFrame(dimension, mainPainel);
    for (int r = 0; r < 9; r += 3) {
      var endRow = r + 2;
      for (int c = 0; c < 9; c += 3) {
        var endCol = c + 2;
        var spaces = getSpacesFromSector(boardService.getSpaces(), c, endCol, r, endRow);
        mainPainel.add(generateSection(spaces));
      }
    }
    addResetButton(mainPainel);
    addCheckGameStatusButton(mainPainel);
    addFinishGameButton(mainPainel);
    mainFrame.revalidate();
    mainFrame.repaint();
  }

  private List<Space> getSpacesFromSector(final List<List<Space>> spaces,
                                          final int initColl, final int endCol,
                                          final int initRow, final int endRow) {
    List<Space> spaceSector = new ArrayList<>();
    for (int r = initRow; r <= endRow; r++) {
      for (int c = initColl; c <= endCol; c++) {
        spaceSector.add(spaces.get(c).get(r));
      }
    }
    return spaceSector;
  }

  private JPanel generateSection(final List<Space> spaces) {
    List<NumberText> fields = new ArrayList<>(spaces.stream().map(NumberText::new).toList());
    fields.forEach(t -> notifierService.subscribe(CLEAR_SPACE, t));
    return new SudokuSector(fields);
  }

  private void addFinishGameButton(JPanel mainPainel) {
    finishGameButton = new FinishGameButton(e -> {
      if (boardService.gameIsFinisher()) {
        JOptionPane.showConfirmDialog(null,
                "Parabens voce conclui o jogo");
        resetButton.setEnabled(false);
        checkGameStatusButton.setEnabled(false);
        finishGameButton.setEnabled(false);
      } else {
        JOptionPane.showMessageDialog(null, "Seu jogo tem algum erro, verifique e tente novamente");
      }
    });

    mainPainel.add(finishGameButton);
  }

  private void addCheckGameStatusButton(JPanel mainPainel) {
    checkGameStatusButton = new CheckGameStatusButton(e -> {
      var hasErrors = boardService.hasErros();
      var gameStatus = boardService.getsStatus();
      var mensagem = switch (gameStatus) {
        case NOW_STARTED -> "O jogo nao foi iniciado";
        case INCOMPLETE -> "O jogo esta incompleto";
        case COMPLETE -> "O jogo esta completo";
      };
      mensagem += hasErrors ? " e contem erros" : " e não contem erros";
      JOptionPane.showMessageDialog(null, mensagem);
    });

    mainPainel.add(checkGameStatusButton);
  }

  private void addResetButton(JPanel mainPainel) {
    resetButton = new ResetButton(e -> {
      var dialogResult = JOptionPane.showConfirmDialog(null,
              "Deseja realmente reiniciar o seu jogo?",
              "Limpar o jogo",
              YES_NO_OPTION,
              QUESTION_MESSAGE);
      if (dialogResult == 0) {
        boardService.reset();
        notifierService.notify(CLEAR_SPACE);
      }
    });
    mainPainel.add(resetButton);
  }

}
