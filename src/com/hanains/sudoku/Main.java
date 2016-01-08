package com.hanains.sudoku;

public class Main {
	public static void main(String[] args) {
		// 게임 알고리즘
		SudokuLogic logic = new SudokuLogic();

		// 게임 프레임
		GameUI frame = new GameUI(logic);

		frame.pack();
		frame.setSize(400, 440);
		frame.setVisible(true);
	}
}
