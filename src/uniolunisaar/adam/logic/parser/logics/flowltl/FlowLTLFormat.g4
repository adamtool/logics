// For parsing the flow ltl formulas.
// @author Manuel Gieseking
grammar FlowLTLFormat;

flowLTL: runFormula EOF;

// flowLTL
runFormula: ltl | 
              '(' phi1=ltl rimp phi2=runFormula ')' |
              runBinary |
              flowFormula;

runBinary: '(' phi1=runFormula op=rbin phi2=runFormula ')';

// flowFormula
flowFormula: forallFlows phi=ltl;

// LTL
ltl: ltlUnary | ltlBinary | tt | ff | atom;
ltlUnary: op=unaryOp phi=ltl;
ltlBinary:  '(' phi1=ltl op=binaryOp phi2=ltl ')';

atom:  id=ID | id=INT;

/* operators */
// LTL
unaryOp: (ltlFinally | globally | next | neg);
binaryOp: (and | or | imp | bimp | until | weak | release );
ltlFinally: 'F' | '◇';
globally: 'G' | '⬜';
next: 'X' | '◯';
neg: 'NEG' | '!' | '¬';
and: 'AND' | '⋏' ;
or: 'OR' | '⋎' ;
imp: 'IMP' | '->' | '→' ;
bimp: 'BIMP' | '<->' | '↔';
until: 'U' | '𝓤';
weak: 'W' | '𝓦';
release: 'R' | '𝓡';

// FlowFormula
forallFlows: 'A' | '𝔸';

// RunFormula
rbin: rand | ror;
rand: 'AND' | '⋀';
ror: 'OR' | '⋁';
rimp: 'IMP' | '->' | '⇒';
tt: 'TRUE' | '⊤';
ff: 'FALSE' | '⊥';

/* Lexer symbols */
INT : '0'..'9'+;
ID  :	('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')*;

COMMENT: (
		'//' ~('\n'|'\r')*
		|   '/*' (. )*? '*/'
	) -> skip;

WS  :   ( ' ' | '\n' | '\r' | '\t') -> skip ;