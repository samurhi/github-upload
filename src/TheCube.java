import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;

public class TheCube {
	
	static private int[] cubes = {2, 1, 2, 1, 1, 3, 1, 2, 1, 2, 1, 2, 1, 1, 1, 1, 1, 1,
			1, 1, 2, 2, 1, 1, 1, 1, 1, 2, 3, 1, 1, 1, 3, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 1};
	static private int[][] seedDir = new int[][] {{1, 0, 0}, {0, 1, 0}, {0, 0, 1}, {-1, 0, 0}, {0, -1, 0}, {0, 0, -1}};
	
	static private boolean isPossibleMove(int[] dir, int[] pos, int[][][] board, int ind) {
		int[] testPos = new int[] {pos[0], pos[1], pos[2]};						//set proxy position for testing
		
		for(int iterations = 0; iterations < cubes[ind]; iterations++) {		//move one space per cube at index
			for(int axis = 0; axis < 3; axis++) {
				testPos[axis] += dir[axis];										//move our testPos one square in dir				
				if(testPos[axis] > 3 || testPos[axis] < 0) return false;		//see if we're out of bounds on each axis
			}
			
			if(board[testPos[0]][testPos[1]][testPos[2]] != 0) return false;	//after 1 square move, see if we are in an occupied square
		}
		return true;															//if not out of bounds or crossed-over, good to go
	}
	static private void makeMove(int[] dir, int[] pos, int[][][] board, int ind, int[][] moves) {
		for(int iterations = 0; iterations < cubes[ind]; iterations++) {		//same logic as above but no proxy position
			for(int axis = 0; axis < 3; axis++) {
				pos[axis] += dir[axis];											//change position
			}
			board[pos[0]][pos[1]][pos[2]] = ind + 1;							//and "leave" a box on the board for crossed-over check
		}
		
		for(int axis = 0; axis < 3; axis++) {
			moves[axis][ind] = dir[axis];										//remember which axis can't be accessed
		}
	}
	
	static private int[] rotate(int[][] moves, int ind, int[] dir, boolean isAttempt, int[] firstTurn) {
		int[] newDir = crossProduct(moves, dir, ind, firstTurn);
		if(isAttempt) {
			moves[3][ind]++;
			moves[4][ind]++;
		}
		return newDir;
	}
	static private int[] crossProduct(int[][] moves, int[] dir, int ind, int[] firstTurn) {
		if(ind < 1) return firstTurn;
		else return new int[] {moves[1][ind - 1]*dir[2] - moves[2][ind - 1]*dir[1], moves[2][ind - 1]*dir[0] - moves[0][ind - 1]*dir[2],
				moves[0][ind - 1]*dir[1] - moves[1][ind - 1]*dir[0]};
	}
	
	static private void regress(int[] dir, int[] pos, int[][][] board, int ind, int[][] moves) {
		for(int iterations = 0; iterations < cubes[ind]; iterations++) {		//same logic makeMove, but in reverse
			board[pos[0]][pos[1]][pos[2]] = 0;									//and "delete" the box on the board for crossed-over check
			for(int axis = 0; axis < 3; axis++) {
				pos[axis] -= moves[axis][ind];									//change position backwards
			}
			
		}
		
		for(int axis = 0; axis < 3; axis++) {
			dir[axis] = moves[axis][ind];
			moves[axis][ind] = 0;												//delete move
		}
	}

	static private int middleNumber(int a, int b, int c) {
        if ((a < b && b < c) || (c < b && b < a))
            return b;
 
        else if ((b < a && a < c) || (c < a && a < b))
        return a;
 
        else
        return c;
	}
	static private boolean isUnique(float[][] solutions, float[] solution) {
		for(int y = 0; y < solutions[0].length; y++) {
			
			if(solutions[0][y] == 0) return true;
			
			for(int x = 0; x < solutions.length; x++) {
				if(solution[x] != solutions[x][y]) break;
				if(x == solutions.length - 1) return false;
			}
		}
		return true;
	}
	
	public static void main(String[] args) {
		int[][][] theCube = new int[4][4][4];
		int[][] moveList = new int[5][cubes.length];
		float[][] solutions = new float[64][256];
		int[][][][] boardSolutions = new int[4][4][4][256];
		int[] direction;
		int[] firstTurn;
		int[] position;
		int index = 0;
		int wins = 0;
		int duplicates = 0;
		long counter = 0;
		
		for(int i = 0; i < theCube.length; i++) {
			for(int j = 0; j < theCube[0].length; j++) {
				for(int k = 0; k < theCube[0][0].length; k++) {
					
					for(int d1 = 0; d1 < seedDir.length; d1++) {
						
						if(!isPossibleMove(seedDir[d1], new int[] {i, j, k}, theCube, index)) continue;
						
						for(int d2 = 0; d2 < seedDir.length; d2++) {
							
							if(d2 == d1 || d2 == (d1 + 3) || d2 == (d1 - 3)) continue;
							
							position = new int[] {i, j, k};
							theCube[i][j][k] = 1;
							direction = seedDir[d1];
							firstTurn = seedDir[d2];
							index = 0;
							
							while(moveList[4][0] <= 1) {												
								
								if(moveList[3][index] > 3) {
									moveList[3][index] = 0;
									index--;
									regress(direction, position, theCube, index, moveList);
									direction = rotate(moveList, index, direction, false, firstTurn);
								}
								else if(isPossibleMove(direction, position, theCube, index)) {
									makeMove(direction, position, theCube, index, moveList);
									direction = rotate(moveList, index, direction, true, firstTurn);
									index++;
								}
								else direction = rotate(moveList, index, direction, true, firstTurn);
								
								counter++;
								if(moveList[0][cubes.length - 1] != 0 || moveList[1][cubes.length - 1] != 0 || moveList[2][cubes.length - 1] != 0) {
									float[] solution = new float[64];
									for(int x = 0; x < theCube.length; x++) {
										for(int y = 0; y < theCube[0].length; y++) {
											for(int z = 0; z < theCube[0][0].length; z++) {
												solution[x*16 + y*4 + z] = (float) (theCube[x][y][z] + 0.1*Math.min(Math.min(x, y), z)
												+ 0.01*middleNumber(x, y, z) + 0.001*Math.max(Math.max(x, y), z));
											}
										}
									}
									Arrays.sort(solution);
									
									if(isUnique(solutions, solution)) {
										for(int x = 0; x < solutions.length; x++) {
											solutions[x][wins] = solution[x];
										}
										for(int x = 0; x < theCube.length; x++) {
											for(int y = 0; y < theCube[0].length; y++) {
												for(int z = 0; z < theCube[0][0].length; z++) {
													boardSolutions[x][y][z][wins] = theCube[x][y][z];
												}
											}
										}
										wins++;
									}
									else duplicates++;
																		
									index--;
									regress(direction, position, theCube, index, moveList);
									direction = rotate(moveList, index, direction, false, firstTurn);
								}
							}
							
							for(int x = 0; x < theCube.length; x++) {
								for(int y = 0; y < theCube[0].length; y++) {
									for(int z = 0; z < theCube[0][0].length; z++) {
										theCube[x][y][z] = 0;
									}
								}
							}
							for(int x = 0; x < moveList.length; x++) {
								for(int y = 0; y < moveList[0].length; y++) {
									moveList[x][y] = 0;
								}
							}
							
							NumberFormat percent = new DecimalFormat("#.##");
							String output = "";
							int progress = ((d2 + 6*d1 + 36*k + 144*j + 576*j + 2304*i) - (d2 + 6*d1 + 36*k + 144*j + 576*j + 2304*i) % 96) / 96;
							float percentage = (d2 + 6*d1 + 36*k + 144*j + 576*j + 2304*i) / 92.16f;
							output += percent.format(percentage);
							output += "% [";
							for(int p = 0; p < 96; p++) {
								if(p > progress) output += " ";
								else output += "=";
							}
							output += "]";
							System.out.print(output + "\r");
						}
					}
				}
			}
		}
		finalSolutions(boardSolutions, wins, duplicates, counter);
		
		System.out.println("done after " + counter + " iterations.");
	}
	
	static private void printBoard(int[][][] board) {
		System.out.println("     _____________");
		System.out.println("    /" + String.format("%-3s", board[0][3][3]) + String.format("%-3s", board[1][3][3]) + String.format("%-3s", board[2][3][3]) + String.format("%-3s", board[3][3][3]) + "/|");
		System.out.println("   /" + String.format("%-3s", board[0][2][3]) + String.format("%-3s", board[1][2][3]) + String.format("%-3s", board[2][2][3]) + String.format("%-3s", board[3][2][3]) + "/ |");
		System.out.println("  /" + String.format("%-3s", board[0][1][3]) + String.format("%-3s", board[1][1][3]) + String.format("%-3s", board[2][1][3]) + String.format("%-3s", board[3][1][3]) + "/  |");
		System.out.println(" /" + String.format("%-3s", board[0][0][3]) + String.format("%-3s", board[1][0][3]) + String.format("%-3s", board[2][0][3]) + String.format("%-3s", board[3][0][3]) + "/   |");
		System.out.println("|¯¯¯¯¯¯¯¯¯¯¯¯|    |");
		System.out.println("|    ________|____|");
		System.out.println("|   /" + String.format("%-3s", board[0][3][2]) + String.format("%-3s", board[1][3][2]) + String.format("%-3s", board[2][3][2]) + String.format("%-3s", board[3][3][2]) + "/|");
		System.out.println("|  /" + String.format("%-3s", board[0][2][2]) + String.format("%-3s", board[1][2][2]) + String.format("%-3s", board[2][2][2]) + String.format("%-3s", board[3][2][2]) + "/ |");
		System.out.println("| /" + String.format("%-3s", board[0][1][2]) + String.format("%-3s", board[1][1][2]) + String.format("%-3s", board[2][1][2]) + String.format("%-3s", board[3][1][2]) + "/  |");
		System.out.println("|/" + String.format("%-3s", board[0][0][2]) + String.format("%-3s", board[1][0][2]) + String.format("%-3s", board[2][0][2]) + String.format("%-3s", board[3][0][2]) + "/   |");
		System.out.println("|¯¯¯¯¯¯¯¯¯¯¯¯|    |");
		System.out.println("|    ________|____|");
		System.out.println("|   /" + String.format("%-3s", board[0][3][1]) + String.format("%-3s", board[1][3][1]) + String.format("%-3s", board[2][3][1]) + String.format("%-3s", board[3][3][1]) + "/|");
		System.out.println("|  /" + String.format("%-3s", board[0][2][1]) + String.format("%-3s", board[1][2][1]) + String.format("%-3s", board[2][2][1]) + String.format("%-3s", board[3][2][1]) + "/ |");
		System.out.println("| /" + String.format("%-3s", board[0][1][1]) + String.format("%-3s", board[1][1][1]) + String.format("%-3s", board[2][1][1]) + String.format("%-3s", board[3][1][1]) + "/  |");
		System.out.println("|/" + String.format("%-3s", board[0][0][1]) + String.format("%-3s", board[1][0][1]) + String.format("%-3s", board[2][0][1]) + String.format("%-3s", board[3][0][1]) + "/   |");
		System.out.println("|¯¯¯¯¯¯¯¯¯¯¯¯|    |");
		System.out.println("|    ________|____|");
		System.out.println("|   /" + String.format("%-3s", board[0][3][0]) + String.format("%-3s", board[1][3][0]) + String.format("%-3s", board[2][3][0]) + String.format("%-3s", board[3][3][0]) + "/ ");
		System.out.println("|  /" + String.format("%-3s", board[0][2][0]) + String.format("%-3s", board[1][2][0]) + String.format("%-3s", board[2][2][0]) + String.format("%-3s", board[3][2][0]) + "/  ");
		System.out.println("| /" + String.format("%-3s", board[0][1][0]) + String.format("%-3s", board[1][1][0]) + String.format("%-3s", board[2][1][0]) + String.format("%-3s", board[3][1][0]) + "/   ");
		System.out.println("|/" + String.format("%-3s", board[0][0][0]) + String.format("%-3s", board[1][0][0]) + String.format("%-3s", board[2][0][0]) + String.format("%-3s", board[3][0][0]) + "/    ");
		System.out.println(" ¯¯¯¯¯¯¯¯¯¯¯¯¯     ");
	}
	static private void printAll(int[][][] board, int x, int y, int z, int[] firstDir, int[] secondDir, int[][] moves, long counter) {
		NumberFormat scientific = new DecimalFormat("0.##E0");
		NumberFormat readable = new DecimalFormat("#,###");
		
		printBoard(board);
		System.out.println("A solution was found after " + readable.format(counter) + " iterations.");
		System.out.println("The starting position was: (" + x + ", " + y + ", " + z + ") and the");
		System.out.println("first direction seeds were: (" + firstDir[0] + ", " + firstDir[1] + ", " + firstDir[2] + ") and (" + secondDir[0] + ", " + secondDir[1] + ", " + secondDir[2] + ")");
		System.out.println("All moves to find the solution are as follows:");
		for(int i = -1; i < 5; i++) {
			for(int j = 0; j < cubes.length; j++) {
				if(i == -1) System.out.print("--|" + String.format("%-6s", j + 1) + "|--");
				else if (moves[i][j] > 99999) System.out.print("--|" + String.format("%-6s", scientific.format(moves[i][j])) + "|--");
				else System.out.print("--|" + String.format("%-6s", moves[i][j]) + "|--");
			}
			System.out.println();
		}
	}
	static private void finalSolutions(int[][][][] allBoards, int wins, int duplicates, long counter) {
		NumberFormat readable = new DecimalFormat("#,###");
		
		System.out.println("After considering " + readable.format(counter) + " possible solutions, ");
		System.out.println(readable.format(wins) + " unique solutions were found with " + readable.format(duplicates) + " duplicates.");
		System.out.println("The unique solutions are as follows:");
		
		int columns = 12;
		int remainder = wins % columns;
		int lines = (wins - remainder) / columns;
		
		for(int l = 0; l < lines; l++) {
			for(int y = 0; y < 24; y++) {
				for(int x = 0; x < columns; x++) {
					printBoards(allBoards, x + columns*l, y);
				}
				System.out.println();
			}
			System.out.println();
			System.out.println();
		}
		for(int y = 0; y < 24; y++) {
			for(int x = 0; x < remainder; x++) {
				printBoards(allBoards, x + columns*lines, y);
			}
			System.out.println();
		}
	}
	static private void printBoards(int[][][][] board, int ind, int line) {
		     if(line ==  0) System.out.print("     _____________   ");
		else if(line ==  1) System.out.print("    /" + String.format("%-3s", board[0][3][3][ind]) + String.format("%-3s", board[1][3][3][ind]) + String.format("%-3s", board[2][3][3][ind]) + String.format("%-3s", board[3][3][3][ind]) + "/|  ");
		else if(line ==  2) System.out.print("   /" + String.format("%-3s", board[0][2][3][ind]) + String.format("%-3s", board[1][2][3][ind]) + String.format("%-3s", board[2][2][3][ind]) + String.format("%-3s", board[3][2][3][ind]) + "/ |  ");
		else if(line ==  3) System.out.print("  /" + String.format("%-3s", board[0][1][3][ind]) + String.format("%-3s", board[1][1][3][ind]) + String.format("%-3s", board[2][1][3][ind]) + String.format("%-3s", board[3][1][3][ind]) + "/  |  ");
		else if(line ==  4) System.out.print(" /" + String.format("%-3s", board[0][0][3][ind]) + String.format("%-3s", board[1][0][3][ind]) + String.format("%-3s", board[2][0][3][ind]) + String.format("%-3s", board[3][0][3][ind]) + "/   |  ");
		else if(line ==  5) System.out.print("|¯¯¯¯¯¯¯¯¯¯¯¯|    |  ");
		else if(line ==  6) System.out.print("|    ________|____|  ");
		else if(line ==  7) System.out.print("|   /" + String.format("%-3s", board[0][3][2][ind]) + String.format("%-3s", board[1][3][2][ind]) + String.format("%-3s", board[2][3][2][ind]) + String.format("%-3s", board[3][3][2][ind]) + "/|  ");
		else if(line ==  8) System.out.print("|  /" + String.format("%-3s", board[0][2][2][ind]) + String.format("%-3s", board[1][2][2][ind]) + String.format("%-3s", board[2][2][2][ind]) + String.format("%-3s", board[3][2][2][ind]) + "/ |  ");
		else if(line ==  9) System.out.print("| /" + String.format("%-3s", board[0][1][2][ind]) + String.format("%-3s", board[1][1][2][ind]) + String.format("%-3s", board[2][1][2][ind]) + String.format("%-3s", board[3][1][2][ind]) + "/  |  ");
		else if(line == 10) System.out.print("|/" + String.format("%-3s", board[0][0][2][ind]) + String.format("%-3s", board[1][0][2][ind]) + String.format("%-3s", board[2][0][2][ind]) + String.format("%-3s", board[3][0][2][ind]) + "/   |  ");
		else if(line == 11) System.out.print("|¯¯¯¯¯¯¯¯¯¯¯¯|    |  ");
		else if(line == 12) System.out.print("|    ________|____|  ");
		else if(line == 13) System.out.print("|   /" + String.format("%-3s", board[0][3][1][ind]) + String.format("%-3s", board[1][3][1][ind]) + String.format("%-3s", board[2][3][1][ind]) + String.format("%-3s", board[3][3][1][ind]) + "/|  ");
		else if(line == 14) System.out.print("|  /" + String.format("%-3s", board[0][2][1][ind]) + String.format("%-3s", board[1][2][1][ind]) + String.format("%-3s", board[2][2][1][ind]) + String.format("%-3s", board[3][2][1][ind]) + "/ |  ");
		else if(line == 15) System.out.print("| /" + String.format("%-3s", board[0][1][1][ind]) + String.format("%-3s", board[1][1][1][ind]) + String.format("%-3s", board[2][1][1][ind]) + String.format("%-3s", board[3][1][1][ind]) + "/  |  ");
		else if(line == 16) System.out.print("|/" + String.format("%-3s", board[0][0][1][ind]) + String.format("%-3s", board[1][0][1][ind]) + String.format("%-3s", board[2][0][1][ind]) + String.format("%-3s", board[3][0][1][ind]) + "/   |  ");
		else if(line == 17) System.out.print("|¯¯¯¯¯¯¯¯¯¯¯¯|    |  ");
		else if(line == 18) System.out.print("|    ________|____|  ");
		else if(line == 19) System.out.print("|   /" + String.format("%-3s", board[0][3][0][ind]) + String.format("%-3s", board[1][3][0][ind]) + String.format("%-3s", board[2][3][0][ind]) + String.format("%-3s", board[3][3][0][ind]) + "/   ");
		else if(line == 20) System.out.print("|  /" + String.format("%-3s", board[0][2][0][ind]) + String.format("%-3s", board[1][2][0][ind]) + String.format("%-3s", board[2][2][0][ind]) + String.format("%-3s", board[3][2][0][ind]) + "/    ");
		else if(line == 21) System.out.print("| /" + String.format("%-3s", board[0][1][0][ind]) + String.format("%-3s", board[1][1][0][ind]) + String.format("%-3s", board[2][1][0][ind]) + String.format("%-3s", board[3][1][0][ind]) + "/     ");
		else if(line == 22) System.out.print("|/" + String.format("%-3s", board[0][0][0][ind]) + String.format("%-3s", board[1][0][0][ind]) + String.format("%-3s", board[2][0][0][ind]) + String.format("%-3s", board[3][0][0][ind]) + "/      ");
		else if(line == 23) System.out.print(" ¯¯¯¯¯¯¯¯¯¯¯¯¯       ");
	}
}
