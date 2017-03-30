package arith.lang;

import java_cup.runtime.Symbol;
import java.io.FileReader;

public class Compiler {

    interface Expr {
        public int interp();
    }

    static class AddExpr implements Expr {
        public final Expr l;
        public final Expr r;

        public AddExpr(Expr l, Expr r) {
            this.l = l;
            this.r = r;
        }

        public int interp() {
            return l.interp() + r.interp();
        }
    }

    static class IntExpr implements Expr {
        public final int n;

        public IntExpr(int n) {
            this.n = n;
        }

        public int interp() {
            return n;
        }
    }

    public static void printTokens(FileReader r) throws Exception {
        Lexer l = new Lexer(r);
        Symbol s = l.next_token();
        while(s.sym != ParserSym.EOF) {
            System.out.println(ParserSym.terminalNames[s.sym]);
            s = l.next_token();
        }
    }

    public static void interp(FileReader r) throws Exception {
        Parser p = new Parser(r);
        Symbol s = p.parse();
        Expr ast = (Expr)s.value;
        System.out.println(ast.interp());
    }

    public static void main(String[] args) throws Exception {
        FileReader r = new FileReader(args[0]);
        printTokens(r);
    //    interp(r);
    }
}




