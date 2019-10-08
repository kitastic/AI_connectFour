import java.io.*;
import java.util.*;

/**
 *
 * @author James Spargo
 * This class controls the game play for the Max Connect-Four game. 
 * To compile the program, use the following command from the maxConnectFour directory:
 * javac *.java
 *
 * the usage to run the program is as follows:
 * ( again, from the maxConnectFour directory )
 *
 *  -- for interactive mode:
 * java MaxConnectFour interactive [ input_file ] [ computer-next / human-next ] [ search depth]
 *
 * -- for one move mode
 * java maxConnectFour.MaxConnectFour one-move [ input_file ] [ output_file ] [ search depth]
 *
 * description of arguments: 
 *  [ input_file ]
 *  -- the path and filename of the input file for the game
 *
 *  [ computer-next / human-next ]
 *  -- the entity to make the next move. either computer or human. can be abbreviated to either C or H. This is only used in interactive mode
 *
 *  [ output_file ]
 *  -- the path and filename of the output file for the game.  this is only used in one-move mode
 *
 *  [ search depth ]
 *  -- the depth of the minimax search algorithm
 *
 *
 */

public class maxconnect4
{
    private static GameBoard currentGame;
    private static AiPlayer calculon;
    public static void main(String[] args)
    {
        // check for the correct number of arguments
        if( args.length != 4 )
        {
            System.out.println("Four command-line arguments are needed:\n"
                    + "Usage: java [program name] interactive [input_file] [computer-next / human-next] [depth]\n"
                    + " or:  java [program name] one-move [input_file] [output_file] [depth]\n");

            exit_function( 0 );
        }

        // parse the input arguments
        String game_mode = args[0].toString();				// the game mode
        String input = args[1].toString();					// the input game file


        // create and initialize the game board
        currentGame = new GameBoard( input );

        // create the Ai Player
        calculon = new AiPlayer();

        /***********************************************************
         * Begin game play
         */
        System.out.print("\nMaxConnect-4 game\n");


        if( game_mode.equalsIgnoreCase( "interactive" ) )
        {
            String nextTurn = args[2].toString();
            int depthLevel = Integer.parseInt( args[3] );

            // set ai to be player 1 or 2 depending on next turn
            if(nextTurn.equalsIgnoreCase("computer-next"))
            {
                // specify ai player number for evaluation function
                calculon.playerNumber = currentGame.getCurrentTurn();
            }
            else if(nextTurn.equalsIgnoreCase("human-next"))
            {
                // if human is next and current turn equals 1, then ai plays next as player 2
                calculon.playerNumber = (currentGame.getCurrentTurn() == 1) ? 2 : 1;
            }
            else
            {
                System.out.println("Next player undetermined: " + nextTurn);
                exit_function(0);
            }

            while(true)
            {
                //print the current game board
                currentGame.printGameBoard();
                // print the current scores
                currentGame.printCurrentScore();
                // if board is full, exit
                if(currentGame.isFull())
                {
                    System.out.println("No more moves to make");
                    return;
                }

                if( nextTurn.equalsIgnoreCase("computer-next"))
                {
                    // choose and make the next move
                    // save board state in file called computer.txt
                    int playColumn = oneMove("computer.txt", depthLevel);
                    nextTurn = "human-next";

                    // display the current game board
                    currentGame.printCurrentMove(playColumn);

                }
                else if(nextTurn.equalsIgnoreCase("human-next"))
                {
                    boolean validChoice = false;
                    int column = 9;

                    while(validChoice == false)
                    {
                        System.out.print("Choose column to place a piece: ");
                        Scanner userInput = new Scanner(System.in);
                        column = userInput.nextInt();

                        if(currentGame.isValidPlay(column))
                            validChoice = true;
                        else
                            System.out.println("Invalid choice, try again");
                    }
                    currentGame.playPiece(column);
                    currentGame.printCurrentMove(column);
                    currentGame.printGameBoardToFile( "human.txt" );
                    nextTurn = "computer-next";
                }
            }


        }
        else if( game_mode.equalsIgnoreCase("one-move"))
        {

            // specify ai player number for evaluation function
            calculon.playerNumber = currentGame.getCurrentTurn();

            System.out.print("game state before move:\n");
            //print the current game board
            currentGame.printGameBoard();
            // print the current scores
            currentGame.printCurrentScore();

            // get the output file name
            String output = args[2].toString();				// the output game file
            int depthLevel = Integer.parseInt( args[3] );   // the depth level of the ai search
            int playColumn = oneMove(output, depthLevel);

            // display the current game board
            currentGame.printCurrentMove(playColumn);

            System.out.print("game state after move:\n");
            currentGame.printGameBoard();

            // print the current scores
            currentGame.printCurrentScore();
        }
        else if( !game_mode.equalsIgnoreCase( "one-move" ) )
        {
            System.out.println( "\n" + game_mode + " is an unrecognized game mode \n try again. \n" );
            return;
        }
        return;
    } // end of main()

    private static int oneMove(String output, int depthLevel)
    {
        // assign worst cases for alpha and beta
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;


        // ****************** this chunk of code makes the computer play
        if( currentGame.getPieceCount() < 42 )
        {

            // AI play - random play
            int playColumn = calculon.dlPruning( currentGame, alpha, beta, depthLevel);

            // play the piece
            currentGame.playPiece( playColumn );
            currentGame.printGameBoardToFile( output );

            return playColumn;
        }
        else
        {
            System.out.println("\nI can't play.\nThe Board is Full\n\nGame Over");
            return 99;
        }
    }

    /**
     * This method is used when to exit the program prematurely.
     * @param value an integer that is returned to the system when the program exits.
     */
    private static void exit_function( int value )
    {
        System.out.println("exiting from MaxConnectFour.java!\n\n");
        System.exit( value );
    }
} // end of class connectFour
