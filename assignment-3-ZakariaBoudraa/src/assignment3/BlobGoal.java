package assignment3.assignment3;

import java.awt.Color;

public class BlobGoal extends Goal{

	public BlobGoal(Color c) {
		super(c);
	}

	@Override
	public int score(Block board) {
		Color[][] flattenedBoard = board.flatten();
		boolean[][] visited = new boolean[flattenedBoard.length][flattenedBoard.length];
		for (int i = 0; i < visited.length; i ++) {
			for (int j = 0; j < visited.length; j ++) {
				visited[i][j] = false;
			}
		}

		int sizeBlob = 0;
		int tempBlobSize = 0;
		for (int i = 0; i < flattenedBoard.length; i ++) {
			for (int j = 0; j < flattenedBoard.length; j ++) {
				if (flattenedBoard[i][j] == this.targetGoal) {
					tempBlobSize = undiscoveredBlobSize(i, j, flattenedBoard, visited);
				}
				if (tempBlobSize > sizeBlob) {
					sizeBlob = tempBlobSize;
				}
			}
		}
		return sizeBlob;
	}

	@Override
	public String description() {
		return "Create the largest connected blob of " + GameColors.colorToString(targetGoal) 
		+ " blocks, anywhere within the block";
	}

	public int undiscoveredBlobSize(int i, int j, Color[][] unitCells, boolean[][] visited) {
		if (i >= unitCells.length || i < 0 || j >= unitCells.length || j < 0) {
			return 0;
		}
		int size = 0;
		if (unitCells[i][j] == this.targetGoal && visited[i][j] == false) {
			visited[i][j] = true;
			size += 1;
			for (int a = i - 1; a <= i + 1; a ++) {
				size += undiscoveredBlobSize(a, j, unitCells, visited);
			}
			for (int b = j - 1; b <= j + 1; b ++) {
				size += undiscoveredBlobSize(i, b, unitCells, visited);
			}
			return size;
		}
		return 0;
	}

}