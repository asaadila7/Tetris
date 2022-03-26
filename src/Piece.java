/*
class piece
makes a boolean representation of a piece and sets colour
 */
public class Piece {
    private static final boolean [] [] [] shapes = { //boolean array of piece shapes
            {{true, true}, {true, false}, {true, false}},
            {{false, true}, {true, true}, {false, true}},
            {{true, true}, {false, true}, {false, true}},
            {{true, true, false}, {false, true, true}},
            {{true}, {true}, {true}, {true}},
            {{true, true}, {true, true}},
            {{false, true, true}, {true, true, false}}
    };

    private boolean [] [] shape; //specific piece shape
    private final int colour; //int corresponding to a colour

    /*
    method piece
    pre: n/a
    post: new piece array and colour
     */
    public Piece () {
        int shape = (int) (Math.random () * 7); //generate piece at random
        this.shape = shapes [shape];
        colour = shape;
    }

    /*
    method setshape
    pre:n/a
    post : resets boolean array for shape to desired array
     */
    public void setShape (boolean[][] shape) {
        this.shape = shape;
    }

    /*
    method getshape
    pre: none
    post: returns shape array
     */
    public boolean [][] getShape() {
        return shape;
    }

    /*
    method getColour
    pre: none
    post: returns integer representation of colour
     */
    public int getColour() {
        return colour;
    }

    /*
    method turn
    pre : boolean determining clockwise / anticlockwise
    post: returns array turned in specific direction
     */
    public boolean [] [] turn (boolean clockwise) {
        boolean [] [] shape = new boolean [this.shape [0].length] [this.shape.length];

        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape [0].length; j++) {
                if (clockwise) {
                    shape [i] [j] = this.shape [Math.abs (j - shape [0].length + 1)] [i];
                } else {
                    shape [i] [j] = this.shape [j] [Math.abs (i - shape.length + 1)];
                }
            }
        }

        return shape;
    }

    /*
    method flatten
    pre : n/a
    post: returns shape array turned horizontal
     */
    public boolean [] [] flatten () {
        if (shape.length > 2) {
            return turn (true);
        } else {
            return shape;
        }
    }
}
