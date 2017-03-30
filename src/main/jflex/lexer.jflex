package arith.lang;
import java_cup.runtime.*;

%%

%class Lexer
%unicode
%cup
%line
%column

%{
  ParserSym sym;

  private Symbol symbol(int type) {
    return new Symbol(type, yyline, yycolumn);
  }
  
  private Symbol symbol(int type, Object value) {
    return new Symbol(type, yyline, yycolumn, value);
  }
%}

LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
WhiteSpace     = {LineTerminator} | [ \t\f]

/* comments */
Comment = {TraditionalComment} | {EndOfLineComment} | {DocumentationComment}

TraditionalComment = "/*" [^*] ~"*/" | "*" "*"+ "/"
// Comment can be the last line of the file, without line terminator.
EndOfLineComment     = "//" {InputCharacter}* {LineTerminator}?
DocumentationComment = "/**" {CommentContent} "*"+ "/"
CommentContent       = ( [^*] | \*+ [^/*] )*

DecIntegerLiteral = 0 | [1-9][0-9]*

%%

<YYINITIAL> {
  /* literals */
  "true"                { return symbol(sym.TRUE); }
  "false"                { return symbol(sym.FALSE); }
  "if"                { return symbol(sym.IF); }
  "then"                { return symbol(sym.THEN); }
  "else"                { return symbol(sym.ELSE); }
  "succ"                { return symbol(sym.SUCC); }
  "pred"                { return symbol(sym.PRED); }
  ";"                { return symbol(sym.SEMI); }
  "("                { return symbol(sym.RPAREN); }
  ")"                { return symbol(sym.LPAREN); }
  {DecIntegerLiteral}   { return symbol(sym.INTV, Integer.parseInt(yytext())); }

  {Comment}             { /* ignore */ }
  
  {WhiteSpace}          { /* ignore */ }
}

/* error fallback */
/*[^]                { throw new Error("Illegal character <" + yytext()+">"); }*/
[^]                { /* ignore */ }
