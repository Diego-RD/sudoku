package ui.custom.screen;

import service.BoardService;
import ui.custom.button.CheckGameStatusButton;
import ui.custom.button.FinishGameButton;
import ui.custom.button.ResetButton;
import ui.custom.frame.MainFrame;
import ui.custom.painel.MainPainel;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

import static javax.swing.JOptionPane.QUESTION_MESSAGE;
import static javax.swing.JOptionPane.YES_NO_OPTION;

public class MainScreen {

  private final static Dimension dimension = new Dimension(600, 600);

  private final BoardService boardService;

  private JButton finishGameButton;
  private JButton checkGameStatusButton;
  private JButton resetButton;


  public MainScreen(Map<String, String> gameConfig) {
    this.boardService = new BoardService(gameConfig);
  }

  public void buildMainScreen() {
    JPanel mainPainel = new MainPainel(dimension);
    JFrame mainFrame = new MainFrame(dimension, mainPainel);
    addResetButton(mainPainel);
    addCheckGameStatusButton(mainPainel);
    addFinishGameButton(mainPainel);
    mainFrame.revalidate();
    mainFrame.repaint();
  }

  private void addFinishGameButton(JPanel mainPainel) {
     finishGameButton = new FinishGameButton(e -> {
      if (boardService.gameIsFinisher()){
        JOptionPane.showConfirmDialog(null,
                "Parabens voce conclui o jogo");
        resetButton.setEnabled(false);
        checkGameStatusButton.setEnabled(false);
        finishGameButton.setEnabled(false);
      }else {
        JOptionPane.showConfirmDialog(null,"Seu jogo tem algum erro, verifique e tente novamente");
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
      JOptionPane.showConfirmDialog(null,
              mensagem);
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
      }
    });
    mainPainel.add(resetButton);
  }

}
