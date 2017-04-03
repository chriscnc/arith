package arith.lang;

import java.util.LinkedList;
import java_cup.runtime.Symbol;
import java.io.FileReader;

public class Compiler {

    interface Term {
        Term eval1() throws NoRuleApplies;
        void traverse();
        boolean isNumerical();
        boolean isVal();
    }

    static class NoRuleApplies extends Exception {}

    static class TmTrue implements Term {
        public Term eval1() throws NoRuleApplies { 
            throw new NoRuleApplies(); 
        }
        
        public void traverse() {
            System.out.println(this.getClass().getName());
        }
        public boolean isNumerical() { return false; }
        public boolean isVal() { return true; }
        public String toString() { return "true"; }
    }

    static class TmFalse implements Term {
        public Term eval1() throws NoRuleApplies { 
            throw new NoRuleApplies(); 
        }
        public void traverse() {
            System.out.println(this.getClass().getName());
        }
        public boolean isNumerical() { return false; }
        public boolean isVal() { return true; }
        public String toString() { return "false"; }
    }

    static class TmZero implements Term {
        public Term eval1() throws NoRuleApplies { 
            throw new NoRuleApplies(); 
        }
        public void traverse() {
            System.out.println(this.getClass().getName());
        }
        public boolean isNumerical() { return true; }
        public boolean isVal() { return true; }
        public String toString() { return "0"; }
    }

    static class TmIf implements Term {
        Term t1;
        Term t2;
        Term t3; 

        public TmIf(Term t1, Term t2, Term t3) {
            this.t1 = t1;
            this.t2 = t2;
            this.t3 = t3;
        }

        public void traverse() {
            System.out.println(this.getClass().getName());
            t1.traverse();
            t2.traverse();
            t3.traverse();
        }
        public Term eval1() throws NoRuleApplies {
            if(t1 instanceof TmTrue) { 
                return t2;
            } else if (t1 instanceof TmFalse) {
                return t3;
            } else {
                Term t1p = t1.eval1();
                return new TmIf(t1p, t2, t3);
            }
        }

        public boolean isNumerical() { return false; }
        public boolean isVal() { return false; }
    }

    static class TmSucc implements Term {
        public Term t1;

        public TmSucc(Term t1) { this.t1 = t1; }

        public Term eval1() throws NoRuleApplies {
            Term t1p = t1.eval1();
            return new TmSucc(t1p);
        }

        public void traverse() {
            System.out.println(this.getClass().getName());
            t1.traverse();
        }

        public boolean isNumerical() { return t1.isNumerical(); }
        public boolean isVal() { return t1.isNumerical(); }

        public String toString() {
            return String.format("%d", TmSucc.toNumber(this));
        }

        public static int toNumber(Term t) {
            if(t instanceof TmSucc) {
                return 1 + toNumber(((TmSucc)t).t1);
            } else {
                return 0;
            }
        }
    }

    static class TmPred implements Term {
        Term t1;
        public TmPred(Term t1) {
            this.t1 = t1;
        }

        public Term eval1() throws NoRuleApplies {
            if(t1 instanceof TmZero) {
                return new TmZero();
            } else if (t1 instanceof TmSucc && t1.isNumerical()) {
                return ((TmSucc)t1).t1; 
            } else {
                Term t1p = t1.eval1();
                return new TmPred(t1p);
            }
        }

        public void traverse() {
            System.out.println(this.getClass().getName());
            t1.traverse();
        }
        public boolean isNumerical() { return false; }
        public boolean isVal() { return false; }
    }

    static class TmIsZero implements Term {
        Term t1;
        public TmIsZero(Term t1) {
            this.t1 = t1;
        }
        public void traverse() {
            System.out.println(this.getClass().getName());
            t1.traverse();
        }

        public Term eval1() throws NoRuleApplies { 
            if(t1 instanceof TmZero) {
                return new TmTrue();
            } else if(t1 instanceof TmSucc && t1.isNumerical()) {
                return new TmFalse();
            } else {
                Term t1p = t1.eval1();
                return new TmIsZero(t1p);
            }
        }

        public boolean isNumerical() { return false; }
        public boolean isVal() { return false; }
    }

    public static void printTokens(FileReader r) throws Exception {
        Lexer l = new Lexer(r);
        Symbol s = l.next_token();
        while(s.sym != ParserSym.EOF) {
            System.out.println(ParserSym.terminalNames[s.sym]);
            s = l.next_token();
        }
    }

    public static Term interpTerm(Term t) {
        try {
            Term tp = t.eval1(); // take a step
            return interpTerm(tp);
        } catch(NoRuleApplies e) {
            return t;
        }
    }

    public static void interp(FileReader r) throws Exception {
        Parser p = new Parser(r);
        Symbol s = p.parse();
        LinkedList<Term> cmds = (LinkedList)s.value;

        for(Term t : cmds) {
//            System.out.println("-------------------------");
//            System.out.println("before...");
//            t.traverse();
//            Term t1 = interpTerm(t);
            System.out.println(interpTerm(t));
//            System.out.println("after...");
//            t1.traverse();
//            System.out.println("result...");
//            System.out.println(t1);
        }
    }

    public static void main(String[] args) throws Exception {
        FileReader r = new FileReader(args[0]);
//        printTokens(r);
        interp(r);
    }
}


