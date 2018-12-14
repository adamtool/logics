// For parsing the flow ctl formulas.
// @author Manuel Gieseking
grammar FlowCTLFormat;

//flowCTL: runFormula EOF;
flowCTL: flowFormula EOF;

// flowLTL
//runFormula: ltl | 
//              '(' phi1=ltl rimp phi2=runFormula ')' |
//              runBinary |
 //             flowFormula;

//runBinary: '(' phi1=runFormula op=rbin phi2=runFormula ')';

// flowFormula
flowFormula: forallFlows '('? phi=ctl ')'?;

// LTL
ctl: ctlUnary | ctlBinary | tt | ff | atom;
ctlUnary: op=unaryOp '('? phi=ctl ')'?;
ctlBinary:  '(' phi1=ctl stdOp=binaryOp phi2=ctl ')' | all'(' phi1=ctl op=until phi2=ctl ')' | exists'(' phi1=ctl op=until phi2=ctl ')';

atom:  id=ID | id=INT;

/* operators */
// CTL
unaryOp: (ex | ax | ef | af | eg | ag | neg);
binaryOp: (and | or | imp | bimp );

exists: 'E';
all: 'A';
ex: exists 'X';
ax: all 'X';
ef: exists 'F';
af: all 'F';
eg: exists 'G';
ag: all 'G';
neg: 'NEG' | '!' | '¬';
and: 'AND' | '⋏' ;
or: 'OR' | '⋎' ;
imp: 'IMP' | '->' | '→' ;
bimp: 'BIMP' | '<->' | '↔';
until: 'U' | '𝓤';
//weak: 'W' | '𝓦';
//release: 'R' | '𝓡';

// FlowFormula
forallFlows: 'All' | '𝔸';

// RunFormula
//rbin: rand | ror;
//rand: 'AND' | '⋀';
//ror: 'OR' | '⋁';
//rimp: 'IMP' | '->' | '⇒';

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