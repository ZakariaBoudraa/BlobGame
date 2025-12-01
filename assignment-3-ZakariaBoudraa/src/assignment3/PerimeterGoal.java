package assignment3;

import java.awt.Color;

public class PerimeterGoal extends Goal{

	public PerimeterGoal(Color c) {
		super(c);
	}

	@Override
	public int score(Block board) {
		if (board != null) {
			Color[][] flattenedBoard = board.flatten();
			int score = 0;
			if (flattenedBoard[0][0] == this.targetGoal) {
				score += 2;
			}
			if (flattenedBoard[flattenedBoard.length - 1][0] == this.targetGoal) {
				score += 2;
			}
			if (flattenedBoard[0][flattenedBoard.length - 1] == this.targetGoal) {
				score += 2;
			}
			if (flattenedBoard[flattenedBoard.length - 1][flattenedBoard.length - 1] == this.targetGoal) {
				score += 2;
			}
			for (int j = 1; j < flattenedBoard.length - 1; j++) {
				if (flattenedBoard[0][j] == this.targetGoal) {
					score += 1;
				}
				if (flattenedBoard[flattenedBoard.length - 1][j] == this.targetGoal) {
					score += 1;
				}
			}
			for (int i = 1; i < flattenedBoard.length - 1; i++) {
				if (flattenedBoard[i][0] == this.targetGoal) {
					score += 1;
				}
				if (flattenedBoard[i][flattenedBoard.length - 1] == this.targetGoal) {
					score += 1;
				}
			}
			return score;
		}
		return 0;
	}

	@Override
	public String description() {
		return "Place the highest number of " + GameColors.colorToString(targetGoal) 
		+ " unit cells along the outer perimeter of the board. Corner cell count twice toward the final score!";
	}

}
