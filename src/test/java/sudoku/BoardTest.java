package sudoku;

// checkstyle-disable-next-line AvoidStarImport
import static org.assertj.core.api.Assertions.*;
// checkstyle-disable-next-line AvoidStarImport
import static org.junit.Assert.*;

import org.junit.Test;

public class BoardTest {
  @Test
  public void testBoardWithInvalidSize() {
    assertThatThrownBy(() -> {
      int[][] board = null;
      new Board(board);
    }).isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("The board size is too small");

    assertThatThrownBy(() -> {
      int[][] board = new int[][]{{}};
      new Board(board);
    }).isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("The board must be a square");

    assertThatThrownBy(() -> {
      int[][] board = new int[][]{{0},{1}};
      new Board(board);
    }).isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("The board size must be a perfect square");

    assertThatThrownBy(() -> {
      int[][] board = new int[][]{
        {0, 0, 0, 0},
        {0, 0, 0},
        {},
        {0, 0}
      };
      new Board(board);
    }).isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("The board must be a square");
  }

  @Test
  public void testBoardWithDuplicate() {
    assertThatThrownBy(() -> {
      int[][] board = new int[][]{
        {1, 0, 0, 1},
        {0, 0, 0, 0},
        {0, 0, 0, 0},
        {0, 0, 0, 0}
      };
      new Board(board);
    }).isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("Value already used");

    assertThatThrownBy(() -> {
      int[][] board = new int[][]{
        {1, 0, 0, 0},
        {0, 0, 0, 0},
        {0, 0, 0, 0},
        {1, 0, 0, 0}
      };
      new Board(board);
    }).isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("Value already used");

    assertThatThrownBy(() -> {
      int[][] board = new int[][]{
        {1, 0, 0, 0},
        {0, 1, 0, 0},
        {0, 0, 0, 0},
        {0, 0, 0, 0}
      };
      new Board(board);
    }).isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("Value already used");
  }

  @Test
  public void testBoardWithInvalidValue() {
    assertThatThrownBy(() -> {
      int[][] board = new int[][]{
        {0, 0, 0, 0},
        {0, 0, 0, 0},
        {0, -1, 0, 0},
        {0, 0, 0, 0}
      };
      new Board(board);
    }).isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("The value specified is invalid");

    assertThatThrownBy(() -> {
      int[][] board = new int[][]{
        {0, 0, 0, 0},
        {0, 0, 0, 0},
        {0, 0, 5, 0},
        {0, 0, 0, 0}
      };
      new Board(board);
    }).isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("The value specified is invalid");
  }

  @Test
  public void testBoardWithValidValue() {
    assertThatCode(() -> {
      int[][] board = new int[][]{
        {1}
      };
      new Board(board);
    }).doesNotThrowAnyException();

    assertThatCode(() -> {
      int[][] board = new int[][]{
        {0}
      };
      new Board(board);
    }).doesNotThrowAnyException();

    assertThatCode(() -> {
      int[][] board = new int[][]{
        {1, 2, 3, 4},
        {3, 4, 1, 2},
        {2, 3, 4, 1},
        {4, 1, 2, 3}
      };
      new Board(board);
    }).doesNotThrowAnyException();

    assertThatCode(() -> {
      int[][] board = new int[][]{
        {3, 1, 0, 0},
        {0, 2, 0, 0},
        {0, 0, 2, 0},
        {0, 0, 1, 3}
      };
      new Board(board);
    }).doesNotThrowAnyException();

    assertThatCode(() -> {
      int[][] board = new int[][]{
        {1, 2, 3, 4, 5, 6, 7, 8, 9},
        {4, 5, 6, 7, 8, 9, 1, 2, 3},
        {7, 8, 9, 1, 2, 3, 4, 5, 6},
        {2, 3, 1, 5, 6, 4, 8, 9, 7},
        {5, 6, 4, 8, 9, 7, 2, 3, 1},
        {8, 9, 7, 2, 3, 1, 5, 6, 4},
        {3, 1, 2, 6, 4, 5, 9, 7, 8},
        {6, 4, 5, 9, 7, 8, 3, 1, 2},
        {9, 7, 8, 3, 1, 2, 6, 4, 5}
      };
      new Board(board);
    }).doesNotThrowAnyException();

    assertThatCode(() -> {
      int[][] board = new int[][]{
        {0, 2, 9, 4, 8, 6, 0, 1, 3},
        {1, 3, 0, 7, 9, 0, 4, 6, 0},
        {0, 6, 4, 0, 3, 1, 7, 9, 2},
        {3, 9, 1, 6, 4, 7, 2, 0, 5},
        {4, 5, 0, 2, 1, 3, 9, 7, 6},
        {0, 7, 2, 9, 0, 8, 0, 4, 1},
        {9, 0, 7, 8, 2, 0, 6, 3, 4},
        {5, 8, 6, 0, 7, 4, 1, 2, 9},
        {2, 4, 3, 1, 6, 0, 8, 5, 7}
      };
      new Board(board);
    }).doesNotThrowAnyException();
  }

  @Test
  public void testBoardCandidatesStream() {
    int[][] board = new int[][]{
      {3, 1, 0, 0},
      {4, 2, 0, 0},
      {0, 0, 2, 0},
      {0, 0, 1, 3}
    };
    Board sudoku = new Board(board);
    assertThat(sudoku.getCandidates(0, 0)).isEmpty();
    assertThat(sudoku.getCandidates(0, 1)).isEmpty();
    assertThat(sudoku.getCandidates(1, 0)).isEmpty();
    assertThat(sudoku.getCandidates(1, 1)).isEmpty();
    assertThat(sudoku.getCandidates(2, 2)).containsOnly(4);
    assertThat(sudoku.getCandidates(3, 2)).containsOnly(4);
    assertThat(sudoku.getCandidates(3, 3)).containsOnly(4);

    assertThat(sudoku.getCandidates(0, 2)).containsOnly(4);
    assertThat(sudoku.getCandidates(0, 3)).containsOnly(2, 4);

    assertThat(sudoku.getCandidates(1, 2)).containsOnly(3);
    assertThat(sudoku.getCandidates(1, 3)).containsOnly(1);

    assertThat(sudoku.getCandidates(2, 0)).containsOnly(1);
    assertThat(sudoku.getCandidates(2, 1)).containsOnly(3, 4);
    assertThat(sudoku.getCandidates(2, 3)).containsOnly(4);

    assertThat(sudoku.getCandidates(3, 0)).containsOnly(2);
    assertThat(sudoku.getCandidates(3, 1)).containsOnly(4);
  }

  @Test
  public void testBoardIsCandidate() {
    int[][] board = new int[][]{
      {3, 1, 0, 0},
      {0, 2, 0, 0},
      {0, 0, 2, 0},
      {0, 0, 1, 3}
    };
    Board sudoku = new Board(board);

    assertThat(sudoku.isCandidate(0, 0, 1)).isFalse();
    assertThat(sudoku.isCandidate(0, 0, 3)).isFalse();
    assertThat(sudoku.isCandidate(0, 0, 4)).isTrue();
    assertThat(sudoku.isCandidate(0, 2, 1)).isFalse();

    assertThat(sudoku.isCandidate(0, 2, 4)).isTrue();
    assertThat(sudoku.isCandidate(0, 3, 2)).isTrue();
    assertThat(sudoku.isCandidate(0, 3, 4)).isTrue();

    assertThat(sudoku.isCandidate(1, 0, 4)).isTrue();
    assertThat(sudoku.isCandidate(1, 2, 3)).isTrue();
    assertThat(sudoku.isCandidate(1, 2, 4)).isTrue();
    assertThat(sudoku.isCandidate(1, 3, 1)).isTrue();
    assertThat(sudoku.isCandidate(1, 3, 4)).isTrue();

    assertThat(sudoku.isCandidate(2, 0, 1)).isTrue();
    assertThat(sudoku.isCandidate(2, 0, 4)).isTrue();
    assertThat(sudoku.isCandidate(2, 1, 3)).isTrue();
    assertThat(sudoku.isCandidate(2, 1, 4)).isTrue();
    assertThat(sudoku.isCandidate(2, 3, 4)).isTrue();

    assertThat(sudoku.isCandidate(3, 0, 2)).isTrue();
    assertThat(sudoku.isCandidate(3, 0, 4)).isTrue();
    assertThat(sudoku.isCandidate(3, 1, 4)).isTrue();
  }

  @Test
  public void testBoardToString() {
    int[][] board = new int[][]{
      {3, 1, 0, 0},
      {0, 2, 0, 0},
      {0, 0, 2, 0},
      {0, 0, 1, 3}
    };
    Board sudoku = new Board(board);
    assertThat(sudoku.toString()).isEqualTo("31__\n_2__\n__2_\n__13\n");
  }

  @Test
  public void testBoardGetSet() {
    int[][] board = new int[][]{
      {3, 1, 0, 0},
      {0, 2, 0, 0},
      {0, 0, 2, 0},
      {0, 0, 1, 3}
    };
    Board sudoku = new Board(board);

    for (int r = 0; r < 4; r++) {
      for (int c = 0; c < 4; c++) {
        assertThat(sudoku.getCell(r, c)).isEqualTo(board[r][c]);
      }
    }

    assertThatCode(() -> {
      int[][] solution = new int[][]{
        {3, 1, 4, 2},
        {4, 2, 3, 1},
        {1, 3, 2, 4},
        {2, 4, 1, 3}
      };
      for (int r = 0; r < 4; r++) {
        for (int c = 0; c < 4; c++) {
          sudoku.setCell(r, c, solution[r][c]);
        }
      }
    }).doesNotThrowAnyException();

    for (int r = 0; r < 4; r++) {
      for (int c = 0; c < 4; c++) {
        assertThat(sudoku.getCandidates(r, c)).isEmpty();
      }
    }
  }


  @Test
  public void testBoardUpdate() {
    int[][] board = new int[][]{
      {3, 1, 0, 0},
      {0, 2, 0, 0},
      {0, 0, 2, 0},
      {0, 0, 1, 3}
    };
    Board sudoku = new Board(board);

    assertThatCode(() -> {
      int[][] update = new int[][]{
        {4, 2, 0, 0},
        {0, 3, 0, 0},
        {0, 0, 3, 0},
        {0, 0, 2, 4}
      };
      sudoku.setCell(0, 0, update[0][0]);
      sudoku.setCell(1, 1, update[1][1]);
      sudoku.setCell(0, 1, update[0][1]);
      sudoku.setCell(3, 3, update[3][3]);
      sudoku.setCell(2, 2, update[2][2]);
      sudoku.setCell(3, 2, update[3][2]);

      for (int r = 0; r < 4; r++) {
        for (int c = 0; c < 4; c++) {
          assertThat(sudoku.getCell(r, c)).isEqualTo(update[r][c]);
        }
      }
    }).doesNotThrowAnyException();

    assertThatCode(() -> {
      int[][] solution = new int[][]{
        {4, 2, 1, 3},
        {1, 3, 4, 2},
        {2, 4, 3, 1},
        {3, 1, 2, 4}
      };
      for (int r = 0; r < 4; r++) {
        for (int c = 0; c < 4; c++) {
          sudoku.setCell(r, c, solution[r][c]);
        }
      }
    }).doesNotThrowAnyException();
  }
}
