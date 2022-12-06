import java.util.ArrayList;

import javax.print.attribute.standard.Sides;

public class ChessBoard {
    private int[] mailbox = {
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1,  0,  1,  2,  3,  4,  5,  6,  7, -1,
        -1,  8,  9, 10, 11, 12, 13, 14, 15, -1,
        -1, 16, 17, 18, 19, 20, 21, 22, 23, -1,
        -1, 24, 25, 26, 27, 28, 29, 30, 31, -1,
        -1, 32, 33, 34, 35, 36, 37, 38, 39, -1,
        -1, 40, 41, 42, 43, 44, 45, 46, 47, -1,
        -1, 48, 49, 50, 51, 52, 53, 54, 55, -1,
        -1, 56, 57, 58, 59, 60, 61, 62, 63, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
    };

    private int mailbox64[] = {
        21, 22, 23, 24, 25, 26, 27, 28,
        31, 32, 33, 34, 35, 36, 37, 38,
        41, 42, 43, 44, 45, 46, 47, 48,
        51, 52, 53, 54, 55, 56, 57, 58,
        61, 62, 63, 64, 65, 66, 67, 68,
        71, 72, 73, 74, 75, 76, 77, 78,
        81, 82, 83, 84, 85, 86, 87, 88,
        91, 92, 93, 94, 95, 96, 97, 98
    };

    boolean slide[] = {false, false, true, true, true, false};
    int offsets[] = {4, 8, 4, 4, 8, 8}; /* knight or ray directions */
    int offset[][] = {
        {  11,  10,  9,  20, 0,  0,  0,  0 }, /* PAWN */
        { -21, -19,-12, -8, 8, 12, 19, 21 }, /* KNIGHT */
        { -11,  -9,  9, 11, 0,  0,  0,  0 }, /* BISHOP */
        { -10,  -1,  1, 10, 0,  0,  0,  0 }, /* ROOK */
        { -11, -10, -9, -1, 1,  9, 10, 11 }, /* QUEEN */
        { -11, -10, -9, -1, 1,  9, 10, 11 }  /* KING */
    };
    
    int colour[];
    int piece[];

    final int BLACKCOLOUR = -1;
    final int EMPTYCOLOUR =  0;
    final int WHITECOLOUR =  1;

    final int EMPTY  = -1;
    final int PAWN   =  0;
    final int KNIGHT =  1;
    final int BISHOP =  2;
    final int ROOK   =  3;
    final int QUEEN  =  4;
    final int KING   =  5;

    boolean castleAvailablity[];
    int enPassantPosition;
    int enPassantTake;

    ArrayList<Integer> attackerMoves;
    ArrayList<Integer> defenderMoves;
    int toPlay = WHITECOLOUR;

    public ChessBoard(){
        LoadFenString("rnbqkbnr/pppppppp/8/8/2qPK3/8/PPPPPPPP/RNBQ1BNR w KQkq - 0 1");
    }

    public void PrintBoard(){
        System.out.println("\n #⎯⎯⎯⎯⎯⎯⎯⎯#");
        for (int i = 0; i < 64; i++){
            if (i % 8 == 0 || i == 0) { System.out.print((int)Math.floor((8 - (i / 8))) + "|"); }
            System.out.print(IntToFenChar(piece[i], colour[i]));
            if ((i + 1) % 8 == 0 && i != 0) { System.out.println("|"); }
        }
        System.out.println(" #⎯⎯⎯⎯⎯⎯⎯⎯# \n  abcdefgh");
        if (toPlay == WHITECOLOUR){ System.out.println("White to Move"); }
        else { System.out.println("Black to Move"); }
    }

    public ArrayList<Integer> GenerateMoves(int side){
        ArrayList<Integer> returnedMoves = new ArrayList<Integer>();

        for (int i = 0; i < 64; i++) {
            if (colour[i] == side) {
                int p = piece[i];
                if (p != PAWN) {
                    for (int j = 0; j < offsets[p]; j++) {
                        for (int n = i;;) { 
                            n = mailbox[mailbox64[n] + offset[p][j]];
                            if (n == -1) { break; }
                            if (colour[n] != EMPTYCOLOUR) {
                                if (colour[n] == -side){ GenMove(returnedMoves, i, n, 1); }
                                else{ break; }
                            }
                            GenMove(returnedMoves, i, n, 1);
                            if (!slide[p]) { break; }
                        }
                    }

                    if (p == KING){
                        if (colour[i] == WHITECOLOUR){
                            if (castleAvailablity[0] == true && piece[i + 1] == EMPTY && piece[i + 2] == EMPTY) { // White King side 
                                GenMove(returnedMoves, i, i + 2, 0);
                            }
                            if (castleAvailablity[1] == true && piece[i - 1] == EMPTY && piece[i - 2] == EMPTY) { // White Queen Side
                                GenMove(returnedMoves, i, i - 2, 0);
                            }
                        }
                        else {
                            if (castleAvailablity[2] == true && piece[i + 1] == EMPTY && piece[i + 2] == EMPTY) { // Black King side 
                                GenMove(returnedMoves, i, i + 2, 0);
                            }
                            if (castleAvailablity[3] == true && piece[i - 1] == EMPTY && piece[i - 2] == EMPTY) { // Black Queen Side
                                GenMove(returnedMoves, i, i - 2, 0);
                            }
                        }
                    }
                } else{
                    int dir = -colour[i];

                    int leftDiag = mailbox[mailbox64[i] + offset[p][0] * dir];
                    int forward = mailbox[mailbox64[i] + offset[p][1] * dir];
                    int rightDiag = mailbox[mailbox64[i] + offset[p][2] * dir];
                    int forward2 = mailbox[mailbox64[i] + offset[p][3] * dir];

                    if (leftDiag != -1 && colour[leftDiag] == dir){ GenMove(returnedMoves, i, leftDiag, 1); } 
                    else if (leftDiag != -1 && leftDiag == enPassantPosition) { GenMove(returnedMoves, i, leftDiag, 1); }

                    if (rightDiag != -1 && colour[rightDiag] == dir){ GenMove(returnedMoves, i, rightDiag, 1); }
                    else if (rightDiag != -1 && rightDiag == enPassantPosition) { GenMove(returnedMoves, i, rightDiag, 1); }

                    if (forward != -1 && colour[forward] == EMPTYCOLOUR){ GenMove(returnedMoves, i, forward, 0);
                        if (forward2 != -1 && colour[forward] == EMPTYCOLOUR){ GenMove(returnedMoves, i, forward2, 0); }
                    }
                }
            }
        }
        return returnedMoves;
    }

    private void GenMove(ArrayList<Integer> list, int fromIndex, int toIndex, int capture){
        list.add(fromIndex);
        list.add(toIndex);
        list.add(capture);
    }

    public boolean MakeMove(int from, int to){
        if (!FindMove(from, to)) { return false; }

        int p = piece[from];
        switch (p){
            case PAWN:
                if (enPassantPosition != -1 && to == enPassantPosition){
                    piece[enPassantTake] = EMPTY;
                    colour[enPassantTake] = EMPTYCOLOUR; 
                }
                else if (Math.abs(from - to) == 16){ 
                    enPassantPosition = from - (int)((from - to) / 2);
                    enPassantTake = to;
                }
                else if ((to <= 7 && colour[from] == WHITECOLOUR) // Promotion Check
                        || (to >= 56 && colour[from] == BLACKCOLOUR)){
                    piece[from] = QUEEN;
                }

                break;
            case KING:
                if (IsAttacked(to) == true) { return false; }

                if (Math.abs(from - to) == 2 && IsAttacked(from) == false){ // Castle Check
                    int direction = (int)((to - from) / 2);
                    MovePiece(to + direction, to - direction);
                }

                if (colour[from] == WHITECOLOUR){
                    castleAvailablity[0] = false;
                    castleAvailablity[1] = false;
                } else {
                    castleAvailablity[2] = false;
                    castleAvailablity[3] = false;
                }

                break;
        }

        int[] piecesCopy = new int[64];
        int[] coloursCopy = new int[64];

        System.arraycopy(piece, 0, piecesCopy, 0, 64);
        System.arraycopy(colour, 0, coloursCopy, 0, 64);

        MovePiece(from, to);

        if (IsAttacked(FindKingIndex(toPlay))){
            System.arraycopy(piecesCopy, 0, piece, 0, 64);
            System.arraycopy(coloursCopy, 0, colour, 0, 64);
            return false;
        }

        toPlay = -toPlay;
        return true;
    }
    
    public boolean FindMove(int from, int to){
        for (int i = 0; i < attackerMoves.size(); i += 3){
            if (attackerMoves.get(i) == from && attackerMoves.get(i + 1) == to){
                return true;
            }
        }
        return false;
    }

    public boolean CheckWinCondition(){
        return false;
    }

    public int FindKingIndex(int colourIn){
        for (int i = 0; i < 64; i++){
            if (piece[i] == KING && colour[i] == colourIn){
                return i;
            }
        }
        return -1;
    }

    public void MovePiece(int from, int to){
        piece[to] = piece[from];
        piece[from] = EMPTY;
        colour[to] = colour[from];
        colour[from] = EMPTYCOLOUR;
    }
    
    boolean IsAttacked(int to){
        for (int i = 0; i < defenderMoves.size(); i += 3){
            if (defenderMoves.get(i + 1) == to && defenderMoves.get(i + 2) == 1){
                return true;
            }
        }
        return false;
    }

    private void LoadFenString(String fen){
        piece = new int[64];
        colour = new int[64];
        int currentIndex = 0;
        int ranksPassed = 0;
        for (int i = 0; i < fen.length(); i++){
            char currentChar = fen.charAt(i);
            if (Character.isDigit(currentChar)){
                int val = Character.getNumericValue(currentChar);
                for (int j = 0; j < val; j++){
                    piece[currentIndex] = EMPTY;
                    colour[currentIndex] = EMPTYCOLOUR;
                    currentIndex += 1;
                }
            }
            else if (currentChar == '/'){
                ranksPassed += 1;
            }
            else {
                piece[currentIndex] = FenCharToInt(currentChar);
                colour[currentIndex] = FenCharToColour(currentChar);
                currentIndex += 1;
            }

            if (currentIndex >= 64 || ranksPassed >= 8){
                break;
            }
        }

        // Split by Spaces
        String[] spaceSplit = fen.split(" ");

        // Which Colour to Move
        char colour = spaceSplit[1].charAt(0);
        if (colour == 'w'){ toPlay = WHITECOLOUR; } 
        else { toPlay = BLACKCOLOUR; }

        // Castle Availability
        castleAvailablity = new boolean[4];
        String castleString = spaceSplit[2];
        for (int i = 0; i < 4; i++){
            if (castleString.charAt(i) != '-'){
                this.castleAvailablity[i] = true;
            }
        }

        // En Passant Target Square
        String enPassantString = spaceSplit[3];
        if (enPassantString.length() == 1){ enPassantPosition = -1;} 
        else { enPassantPosition = CoordToIndex(enPassantString); }
    }

    private int FenCharToInt(char ch){
        switch(Character.toLowerCase(ch)){
            case 'p':
                return PAWN;
            case 'n':
                return KNIGHT;
            case 'b':
                return BISHOP;
            case 'r':
                return ROOK;
            case 'q':
                return QUEEN;
            case 'k':
                return KING;
        }
        return EMPTY;
    }

    private char IntToFenChar(int in, int cl){
        char ch;
        switch(in){
            case PAWN:
                ch = 'p';
                break;
            case KNIGHT:
                ch = 'n';
                break;
            case BISHOP:
                ch = 'b';
                break;
            case ROOK:
                ch = 'r';
                break;
            case QUEEN:
                ch = 'q';
                break;
            case KING:
                ch = 'k';
                break;
            default:
                ch = '-';
                break;
        }
        if (cl == WHITECOLOUR){
            return Character.toUpperCase(ch);
        }
        return ch;
    }

    private int FenCharToColour(char ch){
        if (Character.isUpperCase(ch)){
            return WHITECOLOUR;
        }
        return BLACKCOLOUR;
    }

    public int CoordToIndex(String str){
        int[] coord = new int[2];

        coord[0] = (int) str.charAt(0) - 97;
        coord[1] = Character.getNumericValue(str.charAt(1)) - 1;

        return (7 - coord[1]) * 8 + coord[0];
    }
}
