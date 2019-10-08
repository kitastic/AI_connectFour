import java.util.*;

/**
 * This is the AiPlayer class.  It simulates a minimax player for the max
 * connect four game.
 * The constructor essentially does nothing. 
 * 
 * @author james spargo
 *
 */

public class AiPlayer 
{
    public static int playerNumber;
    /**
     * The constructor essentially does nothing except instantiate an
     * AiPlayer object.
     *
     */

    public AiPlayer() 
    {
        playerNumber = 0;
    }

    /**
     * This method plays a piece randomly on the board
     * @param currentGame The GameBoard object that is currently being used to
     * play the game.
     * @return an integer indicating which column the AiPlayer would like
     * to play in.
     */
    public int findBestPlay( GameBoard currentGame ) 
    {
        // start random play code
        Random randy = new Random();
        int playChoice = 99;

        playChoice = randy.nextInt( 7 );

        while( !currentGame.isValidPlay( playChoice ) )
            playChoice = randy.nextInt( 7 );

        // end random play code

        return playChoice;
    }

    /**
     * This function starts the process of depth limited alpha-beta pruning search tree
     * @param currentGame the current state of the board game
     * @param alpha max value
     * @param beta min value
     * @param depth how far down the search tree to traverse
     * @return index of column that yields the max utility for current player
     */
    public int dlPruning( GameBoard currentGame, int alpha, int beta, int depth)
    {
        Integer[] utility = new Integer[7];
        boolean[] successors = new boolean[7];

        // decide which turn is the current turn's play piece i
        // if currentGame's currentTurn is 1, then next turn is 2,
        // else next turn is 1

        // generate successor nodes, generated from playing a piece in each column
        ArrayList<GameBoard> childNodes = new ArrayList<>();
        for(int column = 0; column <= 6; column++)
        {
            boolean validPlay = currentGame.playPiece(column);
            if(validPlay)
            {
                childNodes.add(new GameBoard(currentGame.getGameBoard()));
                successors[column] = true;
                currentGame.removePiece(column);
            }
        }

        // for each successors in valid columns,
        // calculate minimax for each successor node to choose max value out of them
        int j=0;
        for(int i=0; i< successors.length; i++)
        {
            if(successors[i])
            {
                int value = minimax(childNodes.get(j++), alpha, beta, depth-1, "min");
                utility[i] = value;
            }

        }

        // find largest utility value from utility list
        int maxUtil = Integer.MIN_VALUE;
        int playColumn = 99;

        // find max utility value and corresponding column
        for(int i=0; i<utility.length; i++)
        {
            if ( utility[i] > maxUtil )
            {
               maxUtil = utility[i];
               playColumn = i;
            }
        }
        Arrays.fill(utility, null);
        return playColumn;
    }

    /**
     * This method is recursively called to produce min or max values of each state
     * @param currentGame the current state of the game being analyzed
     * @param alpha max value
     * @param beta  min value
     * @param depth how far down the search try to analyze
     * @param nodeType  specifies current state whether it is a max or min node
     * @return utility value
     */
    public int minimax( GameBoard currentGame, int alpha, int beta, int depth,
                           String nodeType)
    {
        // if terminal node or depth limit reached, return utility value
        if(currentGame.isFull() || depth == 0)
            return currentGame.evaluate(this.playerNumber);

        int v=0;
        if(nodeType.equalsIgnoreCase("max"))
        {
            v = Integer.MIN_VALUE;
            ArrayList<GameBoard> childNodes = new ArrayList<>();

            // generate successor nodes, generated from playing a piece in each column
            for(int column = 0; column <= 6; column++)
            {
                boolean validPlay = currentGame.playPiece(column);
                if(validPlay)
                {
                    childNodes.add(new GameBoard(currentGame.getGameBoard()));
                    currentGame.removePiece(column);
                }
            }

            for(int child = 0; child < childNodes.size(); child++)
            {
                int v1 = minimax(childNodes.get(child), alpha, beta, depth-1, "min");

                v = Math.max(v, v1);
                alpha = Math.max(alpha, v);

                if ( beta <= alpha )
                    break;
            }
            return v;
        }
        else if(nodeType.equalsIgnoreCase("min"))
        {
            v = Integer.MAX_VALUE;
            ArrayList<GameBoard> childNodes = new ArrayList<>();

            // generate successor nodes, generated from playing a piece in each column
            for(int column = 0; column <= 6; column++)
            {
                boolean validPlay = currentGame.playPiece(column);
                if(validPlay)
                {
                    childNodes.add(new GameBoard(currentGame.getGameBoard()));
                    currentGame.removePiece(column);
                }
            }

            for(int child = 0; child < childNodes.size(); child++)
            {
                int v1 = minimax(childNodes.get(child), alpha, beta, depth-1, "max");

                v = Math.min(v, v1);
                beta = Math.min(beta, v1);
                if ( beta <= alpha )
                    break;
            }
            return v;
        }
        return v;
    }



}
