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

%eofval{
    return symbol(sym.EOF);
%eofval}

LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
WhiteSpace     = {LineTerminator} | [ \t\f]

/* comments */
Comment              = {TraditionalComment} | {EndOfLineComment} | {DocumentationComment}
TraditionalComment   = "/*" [^*] ~"*/" | "*" "*"+ "/"
// Comment can be the last line of the file, without line terminator.
EndOfLineComment     = "//" {InputCharacter}* {LineTerminator}?
DocumentationComment = "/**" {CommentContent} "*"+ "/"
CommentContent       = ( [^*] | \*+ [^/*] )*

DecIntegerLiteral    = 0 | [1-9][0-9]*

%%

<YYINITIAL> {
  /* literals */
  "true"                { return symbol(sym.TRUE); }
  "false"               { return symbol(sym.FALSE); }
  "if"                  { return symbol(sym.IF); }
  "then"                { return symbol(sym.THEN); }
  "else"                { return symbol(sym.ELSE); }
  "succ"                { return symbol(sym.SUCC); }
  "pred"                { return symbol(sym.PRED); }
  "iszero"              { return symbol(sym.ISZERO); }
  ";"                   { return symbol(sym.SEMI); }
  "("                   { return symbol(sym.LPAREN); }
  ")"                   { return symbol(sym.RPAREN); }
  {DecIntegerLiteral}   { return symbol(sym.INTV, Integer.parseInt(yytext())); }

  {Comment}             { /* ignore */ }
  
  {WhiteSpace}          { /* ignore */ }
}

/* error fallback */
[^]                { String msg = String.format("Illegal character '%s' found at line: %d, col: %d",
                                    yytext(), yyline, yycolumn);
                     throw new Error(msg); }
//[^]                { /* ignore */ }
