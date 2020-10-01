/**
*@author : Marwa Bouabid
*@version : 08/31/2020
*/


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An Election consists of the candidates running for office, the ballots that 
 * have been cast, and the total number of voters.  This class implements the 
 * ranked choice voting algorithm.
 * 
 * Ranked choice voting uses this process:
 * <ol>
 * <li>Rather than vote for a single candidate, a voter ranks all the 
 * candidates.  For example, if 3 candidates are running on the ballot, a voter
 * identifies their first choice, second choice, and third choice.
 * <li>The first-choice votes are tallied.  If any candidate receives &gt; 50% 
 * of the votes, that candidate wins.
 * <li>If no candidate wins &gt; 50% of the votes, the candidate(s) with the 
 * lowest number of votes is(are) eliminated.  For each ballot in which an
 * eliminated candidate is the first choice, the 2nd ranked candidate is now
 * the top choice for that ballot.
 * <li>Steps 2 &amp; 3 are repeated until a candidate wins, or all remaining 
 * candidates have exactly the same number of votes.  In the case of a tie, 
 * there would be a separate election involving just the tied candidates.
 * </ol>
 */
public class Election {
    // All candidates that were in the election initially.  If a candidate is 
    // eliminated, they will still stay in this array.
    private final Candidate[] candidates;
    
    // The next slot in the candidates array to fill.
    private int nextCandidate;
    
    /**
     * Create a new Election object.  Initially, there are no candidates or 
     * votes.
     * @param numCandidates the number of candidates in the election.
     */
    public Election (int numCandidates) {
        this.candidates = new Candidate[numCandidates];
    }
    
    /**
     * Adds a candidate to the election
     * @param name the candidate's name
     */
    public void addCandidate (String name) {
        candidates[nextCandidate] = new Candidate (name);
        nextCandidate++;
    }
    
    /**
     * Adds a completed ballot to the election.
     * @param ranks A correctly formulated ballot will have exactly 1 
     * entry with a rank of 1, exactly one entry with a rank of 2, etc.  If 
     * there are n candidates on the ballot, the values in the rank array 
     * passed to the constructor will be some permutation of the numbers 1 to 
     * n.
     * @throws IllegalArgumentException if the ballot is not valid.
     */
    public void addBallot (int[] ranks) {
        if (!isBallotValid(ranks)) {
            throw new IllegalArgumentException("Invalid ballot");
        }
        Ballot newBallot = new Ballot(ranks);
        assignBallotToCandidate(newBallot);
    }

    /**
     * Checks that the ballot is the right length and contains a permutation 
     * of the numbers 1 to n, where n is the number of candidates.
     * @param ranks the ballot to check
     * @return true if the ballot is valid.
     */
    private boolean isBallotValid(int[] ranks) {
        if (ranks.length != candidates.length) {
            return false;
        }
        int[] sortedRanks = Arrays.copyOf(ranks, ranks.length);
        Arrays.sort(sortedRanks);
        for (int i = 0; i < sortedRanks.length; i++) {
            if (sortedRanks[i] != i + 1) {
                return false;
            }
        }
        return true;
    }

    /**
     * Determines which candidate is the top choice on the ballot and gives the
     * ballot to that candidate.
     * @param newBallot a ballot that is not currently assigned to a candidate
     */
    private void assignBallotToCandidate(Ballot newBallot) {
        int candidate = newBallot.getTopCandidate();
        candidates[candidate].addBallot(newBallot);
    }
    
    /**
    *@return this method returns the index of the minimum value in an array
    *@param the method takes an array of integers as a parameter
    */
    public int getMinValueIndex(int[] arr)        // Helper function to find minimum (non-eliminated) value index
    {
      int minValue = arr[0];
      int minValue_i = 0;
      for(int i = 1; i < arr.length; i++)

      {
        if(!this.candidates[i].isEliminated() && arr[i] < minValue)
        {
            minValue = arr[i];
            minValue_i = i;
        }
      }
        return minValue_i;
    }

    /**
    *@return this method returns the index of the maximum value in an array
    *@param the method takes an array of integers as a parameter
    */
    public int getMaxValueIndex(int[] arr)        // Helper function to find maximum (non-eliminated) value index
    {
        int maxValue = arr[0];
        int maxValue_i = 0;
        for(int i = 1; i < arr.length; i++){ 
            if(!this.candidates[i].isEliminated() && arr[i] > maxValue) {
                maxValue = arr[i];
                maxValue_i = i;
            }
        }
        return maxValue_i;
    }


    /**
    *@return this method returns true if there is a tie amongst the remaining candidates of an election otherwise it returns false
    *@param the method takes an array of integers as a parameter, in our case it will be top_votes  and an integer, in out case it will be the higest number of first choices in top_votes
    */
    public boolean updateTie(int[] arr, int val){  // checks to see if all (non-eliminated) value in arr is equal to val (in this case the highest num of votes)
        for (int i = 0; i < arr.length; i++){
            if (!this.candidates[i].isEliminated() && arr[i] != val)
                return false;
        }
        return true;
    }

    /**
    *@return this methos returns the number of eliminated candidate at a given point when we call the function 
    *@param the method takes an array of integers as a parameter, in our case it will be top_votes 
    */
    public int numOfEliminated(int[] arr){         // returns number of eliminated candidates
        int counter = 0;
        for (int i = 0; i < arr.length; i++){
            if (!this.candidates[i].isEliminated())
                counter++;
        }
        
        return counter;
    }

    /**
    *@return this method returns the number of ballots in this election
    *@param the method takes an array of integers as a parameter, in our case it will be top_votes 
    */
    public int numOfBallots(int[] arr){
        int result = 0;
        for (int i = 0; i < arr.length; i++){
            result += candidates[i].getVotes();
        }
        return result;
    }
/**
     * Apply the ranked choice voting algorithm to identify the winner.
     * @return If there is a winner, this method returns a list containing just
     * the winner's name is returned.  If there is a tie, this method returns a
     * list containing the names of the tied candidates.
     */

    public ArrayList<String> selectWinner () 
    {
        boolean winner_found = false;        //declaring a boolean to keep track of weather or not we found a winner yet and set it to false
        boolean tie_found = false;           //declaring a boolean to keep track of weather or not we found a tie yet and set it to false
        ArrayList<String> winners = new ArrayList<>();  //creating the ArrayList that will "store" the names of the winners

        int top_votes[] = new int[this.candidates.length];       // initialize an array with the number of top votes per candidate
        for (int i = 0; i < this.candidates.length; i++){        //Adding the number of highest votes of each candidate to an array called top_votes[] with a loop
            top_votes[i] = this.candidates[i].getVotes();  
        }

        int numBallots = numOfBallots(top_votes);                //declaring a variable that "stores" the number of ballots in this election
        while (!winner_found && !tie_found){                     // this is the condition checking for a majority vote (+50% of votes)
            for (int i = 0; i < this.candidates.length; i++){    //for loop to check if the condition applies to each candidates
                if (this.candidates[i].getVotes() > numBallots/2) {
                    winners.add(this.candidates[i].getName());   //if the condition applies to one of the candidates their name is added to the list of winners 
                    return winners;                              //returning the name of the winner
                }
            }

            tie_found = updateTie(top_votes, top_votes[getMaxValueIndex(top_votes)]); //checks to see if all (non-eliminated) values in top_votes are equal to the highest num of votes
            winner_found = ((top_votes.length - numOfEliminated(top_votes)) == 1); //winner_found checks to see if there is only one candidate left in the competition

            if (tie_found){                                     // if tie add the names all non-eliminated candidates to winner list
                for (int i = 0; i < top_votes.length; i++){
                    if (!this.candidates[i].isEliminated())
                        winners.add(this.candidates[i].getName());
                }
            }
            else if (winner_found){                             // if winner found add name of the winner
                winners.add(this.candidates[getMaxValueIndex(top_votes)].getName());
            }
            else {
                int least_votes_i = getMinValueIndex(top_votes);                       //variable that "stores" the index of the lowest number of first choices
                List<Ballot> temp = this.candidates[least_votes_i].eliminate();       // here we look for and eliminate the candidate with the least num of top votes
                for (int i = 0; i < temp.size(); i++){
                    Ballot ballot = temp.get(i);
                    ballot.eliminateCandidate(ballot.getTopCandidate());
                    top_votes[ballot.getTopCandidate()]++;         // for each ballot, find next top ranked candidate and add to their total top_votes
                }                                                   
            }

            //counter++;                                            //adds to the number of eliminations
        }

        return winners;
    }
}