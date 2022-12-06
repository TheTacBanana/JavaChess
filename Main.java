import java.util.Scanner;

public class Main{
    public static void main(String[] args){
        Scanner in = new Scanner(System.in);
        ChessBoard CB = new ChessBoard();

        while (true){
            CB.PrintBoard();
            CB.attackerMoves = CB.GenerateMoves(CB.toPlay);
            CB.defenderMoves = CB.GenerateMoves(-CB.toPlay);

            boolean result = CB.MakeMove(CB.CoordToIndex(in.next()), CB.CoordToIndex(in.next()));
            if (!result){
                System.out.println("Invalid Move");
            }

            //CB.CheckWinCondition();
        }
    }
}
