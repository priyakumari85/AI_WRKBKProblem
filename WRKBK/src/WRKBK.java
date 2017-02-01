import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


/**
 * @author priya
 *
 */


class Moves{
	int row;
	int col;
	double heuristic;
}

class StateList{
	int states;
	double heuristic;
}

public class WRKBK {
	
	//Lists to store the states and various moves
	public static List<String> TestCaseName = new ArrayList<String>();
	public static List<Integer> StateSpaceInit = new ArrayList<Integer>();
	public static StateList currentState = new StateList();
	
	//String to generate output
	public static String Output = "";
	public static String MovesAndTC = "";
	public static String Moves;
	public static String Summary = "TestCase" +"\t" + " Moves" + "\t" + "States Explored"+ "\n"+"--------------------------------------------------------" +"\n";
	
	//Variables to define state of game
	static final char GAME_STATE_CONTINUE = 'c';
    static final char GAME_STATE_STALEMATE = 's';
    static final char GAME_STATE_MATE = 'm';
	private static char gameState = GAME_STATE_CONTINUE; 
   
	//Constants to for rows and columns
	static final int ROW = 8, COL = 8;
	public static int SEXPL = 0;
	public static int degreeOfFreeom = 0;

	public static void main(String[] args) {
		
			
		Moves wk = new Moves();			
		Moves wr = new Moves();	
		Moves bk = new Moves();
		
		//reads input file
		String fileName = args[0];
		ReadInputFile(fileName);
		
        Output = Output.concat(StateSpaceInit.size()+"\n");

        // Loop for various games
		for (int i = 0; i < StateSpaceInit.size(); i++) {
			
			MovesAndTC= "";
			Moves = "";
			MovesAndTC = MovesAndTC.concat("-----------------------------------------------------");
			MovesAndTC = MovesAndTC.concat("\n").concat(TestCaseName.get(i)).concat("\n");
			System.out.println("-----------------------------------------------------");
			System.out.println(TestCaseName.get(i));
			currentState.states = StateSpaceInit.get(i);
			
			//Since we are playing multiple game restting to 0 at the start of game
			SEXPL=0;
			
			
			
			List<Integer> StateSpace = new ArrayList<Integer>(); 
			paint(currentState.states);
						
			do {
				
				String number = String.valueOf(currentState.states);

				wk.row = Character.digit(number.charAt(0), 10);
				wk.col = Character.digit(number.charAt(1), 10);

				wr.row = Character.digit(number.charAt(2), 10);
				wr.col = Character.digit(number.charAt(3), 10);

				bk.row = Character.digit(number.charAt(4), 10);
				bk.col = Character.digit(number.charAt(5), 10);
				
				
				//This checks if at the start of next move board is already in check
				gameState = CheckMate(bk, wk, wr);

				if (gameState == GAME_STATE_CONTINUE) {
					
					degreeOfFreeom = CalculateDegreeOfFreedom(bk,wr);

					char pieceInAttack = KingAttackingPosition(bk, wk, wr);

					
					// ROOKs movement under attack
					//doesnt consider any move of the king as the preferance is given to saving rook
					if (pieceInAttack == 'r') {
						List<StateList> StateSpaceTemp = new ArrayList<StateList>();
						List<Moves> wrMove = RookMovesGenerator(bk, wk,wr);
						if (wrMove.size()>0) {
							for (Moves mwr : wrMove) {
								List<Moves> bkMoves = KingsMovesGenerator(bk);
								Moves bkMv = ReturnBlackValidMove(wk, mwr,
										bkMoves);
								if (bkMv.row > 0 && bkMv.col > 0) {
									StateList st = new StateList();
									st.states = GenerateStateStrtoInt(wk, mwr,
											bkMv);
									;
									if (!StateSpace.contains(st.states)) {
										st.heuristic = CalcHeuristic(wk, bkMv,
												degreeOfFreeom);
										StateSpaceTemp.add(st);
									}
								}
							}
						}
						currentState = ChoseMinHeuristicMove(StateSpaceTemp);
						StateSpace.add(currentState.states);
						paint(currentState.states);
					}
					
					
					// White Kings movement under attack
					//doesnt consider any move of the rook as the preferance is given to saving King
					if (pieceInAttack == 'k') {
						List<StateList> StateSpaceTemp = new ArrayList<StateList>();
						List<Moves> wkMove = KingsMovesGenerator(wk);
						List<Moves> wkValidMoves = ReturnValidMovesWK(bk, wr, wkMove);
						if (wkValidMoves.size()>0) {
							for (Moves mwk : wkValidMoves) {
								List<Moves> bkMoves = KingsMovesGenerator(bk);
								Moves bkMv = ReturnBlackValidMove(mwk, wr,
										bkMoves);
								if (bkMv.row > 0 && bkMv.col > 0) {
									StateList st = new StateList();
									st.states = GenerateStateStrtoInt(mwk, wr,
											bkMv);
									;
									if (!StateSpace.contains(st.states)) {
										st.heuristic = CalcHeuristic(mwk, bkMv,
												degreeOfFreeom);
										StateSpaceTemp.add(st);
									}
								}
							}
						}
						currentState = ChoseMinHeuristicMove(StateSpaceTemp);
						StateSpace.add(currentState.states);
						paint(currentState.states);

					}

					//Moves if the non of the white peice is under attack and game is not CheckMate 
					if (pieceInAttack == 'n') {
						
						List<StateList> StateSpaceTemp = new ArrayList<StateList>();
						List<Moves> wrMove = RookMovesGenerator(bk, wk, wr);

						if (wrMove.size()>0) {
							// First check if any of the rooks move can give check mate
							for (Moves wrCM : wrMove) {
								if (CheckMate(bk, wk, wrCM) == GAME_STATE_MATE) {
									gameState = GAME_STATE_MATE;
									StateList st = new StateList();
									st.states = GenerateStateStrtoInt(wk, wrCM,
											bk);
									st.heuristic = 0.0;
									currentState = st;
									paint(currentState.states);
									break;
								}
							}
						}
						if (gameState == GAME_STATE_CONTINUE) {
							if (wrMove.size()>0) {
								for (Moves mwr : wrMove) {

									List<Moves> bkMoves = KingsMovesGenerator(bk);
									Moves bkMv = ReturnBlackValidMove(wk, mwr,
											bkMoves);

									if (bkMv.row > 0 && bkMv.col > 0) {
										int _degreeOfFreedom = CalculateDegreeOfFreedom(
												bkMv, mwr);
										if (degreeOfFreeom > _degreeOfFreedom) {
											StateList st = new StateList();
											st.states = GenerateStateStrtoInt(
													wk, mwr, bkMv);
											if (!StateSpace.contains(st.states)) {
												st.heuristic = CalcHeuristic(
														wk, mwr,
														_degreeOfFreedom);
												StateSpaceTemp.add(st);
											}
										}
									}
								}
							}// End of checking for rook
							
								List<Moves> wkMove = KingsMovesGenerator(wk);
								List<Moves> wkValidMoves = ReturnValidMovesWK(bk, wr, wkMove);
								if (wkValidMoves.size()>0) {
									for (Moves mwk : wkValidMoves) {
										List<Moves> bkMoves = KingsMovesGenerator(bk);
										Moves bkMv = ReturnBlackValidMove(mwk,
												wr, bkMoves);
										if (bkMv.row > 0 && bkMv.col > 0) {
											StateList st = new StateList();
											st.states = GenerateStateStrtoInt(
													mwk, wr, bkMv);
											if (!StateSpace.contains(st.states)) {
												st.heuristic = CalcHeuristic(
														mwk, bkMv,
														degreeOfFreeom);
												StateSpaceTemp.add(st);
											}
										}
									}
								}// end of white king move
							
							//from all the collected WR and WK chose with min heuristic
							if (StateSpaceTemp.size() >0 ) {
								currentState = ChoseMinHeuristicMove(StateSpaceTemp);
								StateSpace.add(currentState.states);
								paint(currentState.states);
							}
						}
					}
				} else if (gameState == GAME_STATE_MATE) {
					break;
				}

			} while (gameState == GAME_STATE_CONTINUE && SEXPL <= 10000);
			
			
			
			//String Manipulation to write on console window and 
			if(gameState == GAME_STATE_MATE){
				System.out.println("Total Moves     :" + StateSpace.size());
				System.out.println("State Explored  :" + SEXPL);
				
				MovesAndTC = MovesAndTC.concat("Total Moves     :" + StateSpace.size() + "\n");
				MovesAndTC = MovesAndTC.concat("State Explored  :" + SEXPL + "\n\n");
				
				Output = Output.concat(MovesAndTC).concat(Moves);
				Summary = Summary.concat(TestCaseName.get(i)+"\t\t\t").concat(StateSpace.size()+"\t\t").concat(SEXPL + "\t\t").concat("\n");
			}
			else{
				System.out.println("Total Moves     :" + "NA");
				System.out.println("State Explored  :" + SEXPL);
				
				MovesAndTC = MovesAndTC.concat("Total Moves     :" + "NA" + "\n");
				MovesAndTC = MovesAndTC.concat("State Explored  :" + SEXPL + "\n\n");
				
				Output = Output.concat(MovesAndTC).concat(Moves);
				Summary = Summary.concat(TestCaseName.get(i)+"\t\t").concat("NA"+"\t\t").concat(SEXPL + "\t\t").concat("\n");
			
			}
		}
		System.out.print(Summary);
		GenerateOutputFile();
	}
	
	//Convert object to state
	public static int GenerateStateStrtoInt(Moves wk,Moves wr, Moves bk){
		return Integer.valueOf(String
				.valueOf(wk.row)
				+ String.valueOf(wk.col)
				+ String.valueOf(wr.row)
				+ String.valueOf(wr.col)
				+ String.valueOf(bk.row)
				+ String.valueOf(bk.col));
	}
	
	//Function to put output on console
	public static void paint(int state) {
		
		String number = String.valueOf(state);
		
		int WKX = Character.digit(number.charAt(0), 10);
		int WKY = Character.digit(number.charAt(1), 10);
		
		
		int WRX = Character.digit(number.charAt(2), 10);
		int WRY = Character.digit(number.charAt(3), 10);
		
		int BKX = Character.digit(number.charAt(4), 10);
		int BKY = Character.digit(number.charAt(5), 10);
		
		
		
		for (int row = 1; row <= ROW; row++) {
		    for (int col = 1; col <= COL; col++) {
		    	System.out.print("  ");
		    	if(row == WRX && col == WRY){
		    		 System.out.print("WR");
		    		 Moves = Moves.concat("WR");
		    	} else if(row == WKX && col == WKY){
		    		 System.out.print("WK");
		    		 Moves = Moves.concat("WK");
		    	} else if(row == BKX && col == BKY){
		    		 System.out.print("BK");
		    		 Moves = Moves.concat("BK");
		    	} else { System.out.print("--");
		    		Moves = Moves.concat("--");
		    	}
		    	Moves = Moves.concat(" ");
		    }
		    System.out.println();
		    Moves = Moves.concat("\n");
		}
		System.out.println();
		System.out.println();
		
		Moves = Moves.concat("\n").concat("\n");
	}
	
	
	//Generates Valid moves for black king
	public static List<Moves> KingsMovesGenerator(Moves m){
		
	     int rowNbr[] = new int[] {-1, -1, -1,  0, 0,  1, 1, 1};
	     int colNbr[] = new int[] {-1,  0,  1, -1, 1, -1, 0, 1};
	     
	     List<Moves> kingMoves = new ArrayList<Moves>();
	     
	     for (int k = 0; k < 8; ++k){
	    	 int nrow = m.row + rowNbr[k];
	    	 int ncol = m.col + colNbr[k];
		     if((nrow >= 1) && (nrow <= ROW) && (ncol >= 1) && (ncol <= COL)){
		    	 Moves mv = new Moves();
		    	 mv.row = nrow;
		    	 mv.col = ncol;
		    	// mv.heuristic = 66;
		    	 kingMoves.add(mv);
		     }
	     }
	     
	     return kingMoves;
	}
		
	//Generate all rooks move
	public static List<Moves> RookMovesGenerator(Moves currentBK, Moves currentWK, Moves m){
		
		 List<Moves> rookMoves = new ArrayList<Moves>();
		 
		 //Moving Left
		 for (int k = 1; k <= 8; k++){
			 int ncol = m.col - k;
			 if((ncol >= 1) && (ncol <= COL)){
				 boolean add = true;
		    	 Moves mv = new Moves();
		    	 mv.row = m.row;
		    	 mv.col = ncol;
		    	 if(mv.row == currentWK.row && mv.col == currentWK.col)
		    		 break;
		    	 if(mv.row == currentBK.row && mv.col == currentBK.col)
		    		 break;
		    	 if(add)
		    		 rookMoves.add(mv);
		     }
		 }
		 
		 //Moving Right
		 for (int k = 1; k <= 8; k++){
			 int ncol = m.col + k;
			 if((ncol >= 1) && (ncol <= COL)){
				 boolean add = true;
		    	 Moves mv = new Moves();
		    	 mv.row = m.row;
		    	 mv.col = ncol;
		    	 if(mv.row == currentWK.row && mv.col == currentWK.col)
		    		 break;
		    	 if(mv.row == currentBK.row && mv.col == currentBK.col)
		    		 break;
		    	 if(add)
		    		 rookMoves.add(mv);
		     }
		 }
		 
	 
		 
		 //Moving Up
		 for (int k = 1; k <= 8; k++){
			 int nrow = m.row - k;
			 if((nrow >= 1) && (nrow <= ROW)){
				 boolean add = true;
		    	 Moves mv = new Moves();
		    	 mv.row = nrow;
		    	 mv.col = m.col;
		    	 if(mv.col == currentWK.col && mv.row == currentWK.row)
		    		 break;
		    	 if(mv.col == currentBK.col && mv.row == currentBK.row)
		    		 break;
		    	 if(add)
		    		 rookMoves.add(mv);
		     }
		 }
		 
		 //Moving Down
		 for (int k = 1; k <= 8; k++){
			 int nrow = m.row + k;
			 if((nrow >= 1) && (nrow <= ROW)){
				 boolean add = true;
		    	 Moves mv = new Moves();
		    	 mv.row = nrow;
		    	 mv.col = m.col;
		    	 if(mv.col == currentWK.col && mv.row == currentWK.row)
		    		 break;
		    	 if(mv.col == currentBK.col && mv.row == currentBK.row)
		    		 break;
		    	 if(add)
		    		 rookMoves.add(mv);
		     }
			 
		 }
		 
		List<Moves> BKingMoves = KingsMovesGenerator(currentBK);
		List<Moves> MovesTobeRemoved = new ArrayList<Moves>();
			
		if (rookMoves.size()>0 && BKingMoves.size()>0) {
			for (Moves wr : rookMoves) {
				for (Moves bk : BKingMoves) {
					if (wr.col == bk.col && wr.row == bk.row) {
						MovesTobeRemoved.add(wr);
					}
				}
			}
		}
		
		if (MovesTobeRemoved.size()>0) {
			for (Moves rm : MovesTobeRemoved) {
				rookMoves.remove(rm);
			}
		}
		return rookMoves;
	}
	
	
	//Removes any position which will bring white king under attack
	public static List<Moves> ReturnValidMovesWK(Moves currentBK, Moves currentWR, List<Moves> genWhiteMoves){
		
		List<Moves> BKingMoves = KingsMovesGenerator(currentBK);
		List<Moves> MovesTobeRemoved = new ArrayList<Moves>();
		
		int index = 0;
		if (genWhiteMoves.size()>0 && BKingMoves.size()>0) {
			for (Moves m : genWhiteMoves) {
				for (Moves w : BKingMoves) {
					if (w.col == m.col && w.row == m.row) {
						MovesTobeRemoved.add(m);
					}

					if (m.row == currentWR.row || m.col == currentWR.col) {
						MovesTobeRemoved.add(m);
					}

				}
			}
		}
		
		if (MovesTobeRemoved.size()>0) {
			for (Moves m : MovesTobeRemoved) {
				genWhiteMoves.remove(m);
			}
		}
		return genWhiteMoves;
	}
	
	//Generates Valid moves for black king
	// then removes any position which will bring Black king under attack and then calculates heuristic
	// any postion which is attacking is given preferance
	public static Moves ReturnBlackValidMove(Moves currentWK, Moves currentWR, List<Moves> genBlackMoves){
		List<Moves> WKingMoves = KingsMovesGenerator(currentWK);
		List<Moves> MovesTobeRemoved = new ArrayList<Moves>();
		
		
		if (genBlackMoves.size()>0) {
			for (Moves attackPos : genBlackMoves) {
				if (attackPos.row == currentWR.row
						&& attackPos.col == currentWR.col) {
					return attackPos;
				}
				if (attackPos.row == currentWK.row
						&& attackPos.col == currentWK.col) {
					return attackPos;
				}
			}
		}
		
		if (genBlackMoves.size()>0) {
			// remove moves colliding with white king and rook
			for (Moves m : genBlackMoves) {
				if (WKingMoves.size()>0) {
					for (Moves w : WKingMoves) {
						if (w.col == m.col && w.row == m.row)
							MovesTobeRemoved.add(m);
					}
				}
				if (currentWR.col == m.col || currentWR.row == m.row)
					MovesTobeRemoved.add(m);
			}
		}
		
		if (MovesTobeRemoved.size()>0) {
			for (Moves m : MovesTobeRemoved) {
				genBlackMoves.remove(m);
			}
		}
		Moves min = new Moves();
		min.row = 0;
		min.col = 0;
		
		if(genBlackMoves.size()>0){
			for(Moves m : genBlackMoves){
				m.heuristic = ((Math.abs(m.row - 5)*5)+(Math.abs(m.col - 5)*3) +((m.row+m.col)*0.1));
			}
			
			min = genBlackMoves.get(0);
	        for (Moves m : genBlackMoves){
	            min = min.heuristic < m.heuristic ? min : m;
	        }
		}
		
		return min;
		
	}
	
	
	//this function finds next state which has minimum cost
    public static StateList ChoseMinHeuristicMove(List<StateList> tobeSearched){
    	StateList min = new StateList() ;
    	
    	if (tobeSearched.size()>0) {
			min = tobeSearched.get(0);
			for (StateList m : tobeSearched) {
				min = min.heuristic < m.heuristic ? min : m;
			}
		}
		return min;
    }
    
    
    //Checks if the board is in Mate, Stalemate or check condition
    public static char CheckMate(Moves BKMoves,Moves WKMoves, Moves WRMoves){
    	
    	List<Moves> bkMovesCM = KingsMovesGenerator(BKMoves);
		Moves bKVMovesCM = ReturnBlackValidMove(WKMoves,WRMoves,bkMovesCM);
		
		if(bKVMovesCM.row == 0 || bKVMovesCM.col == 0){
			return 'm';
		}
		else if((bKVMovesCM.row != WRMoves.row || bKVMovesCM.col != WRMoves.col)&& (bKVMovesCM.row == 0 || bKVMovesCM.col == 0)){
			return 's';
		}
		else{
			return 'c';
		}

    }
    
    //Gives attacking position for the black king
    public static char KingAttackingPosition(Moves BKMoves,Moves WKMoves, Moves WRMoves){
    	char peiceInAttack = 'n';
    	List<Moves> bkMovesAM = KingsMovesGenerator(BKMoves);
    	
    	if (bkMovesAM.size()> 0) {
			// Looking ahead
			for (Moves attackPos : bkMovesAM) {
				if (attackPos.row == WKMoves.row
						&& attackPos.col == WKMoves.col) {
					peiceInAttack = 'k';
				}
				if (attackPos.row == WRMoves.row
						&& attackPos.col == WRMoves.col) {
					peiceInAttack = 'r';
				}
			}
		}
		return peiceInAttack;    	
    }

    //Function to integrate all the different heuristics
    public static double CalcHeuristic(Moves wk, Moves bk, int degreeOfFreedom){
    	
    	SEXPL++;
    	double heuristic = 0.0;
    	heuristic = Math.min(Math.min(bk.row-1,8-bk.row),Math.min(bk.col-1, 8-bk.col)); //distance of Black king from corner
    	heuristic = heuristic + (Math.abs(bk.row-wk.row)+ Math.abs(bk.col-wk.col))*0.10; // Manhattan distance of black and white king
    	heuristic = heuristic + degreeOfFreedom*0.30; // accounts how many row & columns Black king can move given rooks position
    	return heuristic;
    }

    //function to generate output file
    public static void GenerateOutputFile(){
		
		try {
			
			File file = new File("output.txt");

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
			Output = Output.concat("\n\n\n").concat("SUMMARY \n").concat(Summary);
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(Output);
			bw.close();

			System.out.println("Done");

		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    
    //Function to read input file
    public static void ReadInputFile(String fileName){
		BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(fileName));
                String x;
                int numberOfTestCase = Integer.parseInt(br.readLine());
                while ( (x = br.readLine()) != null ) {    	
	                	if(!x.equals("")){
	                	   TestCaseName.add(x);
	                	   int wk_row= 0 ,wk_col= 0,wr_row= 0,wr_col= 0,bk_row= 0,bk_col= 0;
	                	   for(int i=0; i<8; i++){
	                		   String[] line = br.readLine().split(" ");
	                		   for(int j=0; j<8; j++){
	                			    if(line[j].equals("WK"))
	                			    	{wk_row=i+1;wk_col=j+1;}
	                			    if(line[j].equals("WR"))
	                			        {wr_row=i+1;wr_col=j+1;}
	                			    if(line[j].equals("BK"))
	                			    	{bk_row=i+1;bk_col=j+1;}
	                		   }
	                	   }
	                	   StateSpaceInit.add(Integer.valueOf(String.valueOf(wk_row) 
	                			   + String.valueOf(wk_col)
	                			   +String.valueOf(wr_row)
	                			   +String.valueOf(wr_col)
	                			   +String.valueOf(bk_row)
	                			   +String.valueOf(bk_col)));
	                  	}

                } 
            
        } catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + fileName + "'");                
        }
        catch(IOException ex) {
            System.out.println("Error reading file '" + fileName + "'");
        }
        catch(Exception ex) {
            System.out.println("Error: " + ex);
        }
    }

    
    //Calculates how many rows and columns Black King can move given White Rooks position
    public static int CalculateDegreeOfFreedom(Moves bk, Moves wr){
    	//everytime a state is explored
    	SEXPL++;
    	int counter = 0;
    	
    		// calculate degree of freedom of black king with rooks move
	    		if((bk.row <= wr.row || bk.col <= wr.col)){
	    			counter = 0;
	    			for(int i = 1; i < wr.row; i++){
	    				for(int j = 1; j < wr.col; j++){
	    					counter++;
	    				}
	    			}
	    		}
	    		if(bk.row < wr.row && bk.col > wr.col){
	    			counter = 0;
	    			for(int i = 1; i < wr.row; i++){
	    				for(int j = wr.col+1; j < 9; j++){
	    					counter++;
	    				}
	    			}
	    			
	    		}
	    		if(bk.row > wr.row && bk.col < wr.col){
	    			counter = 0;
	    			for(int i = wr.row+1; i < 9; i++){
	    				for(int j = 1; j < wr.col; j++){
	    					counter++;
	    				}
	    			}
	    		}
	    		
	    		if(bk.row > wr.row && bk.col > wr.col){
	    			counter = 0;
	    			for(int i = wr.row+1; i < 9; i++){
	    				for(int j = wr.col+1; j < 9; j++){
	    					counter++;
	    				}
	    			}
	    		}
	    	return counter;
    }
  

}


