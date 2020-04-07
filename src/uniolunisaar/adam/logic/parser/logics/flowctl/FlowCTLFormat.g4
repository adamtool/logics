// For parsing the flow ctl formulas.
// @author Manuel Gieseking
grammar FlowCTLFormat;

flowCTL: runFormula EOF;

// flowCTL
runFormula: ctl | runUnary | runBinary | flowFormula;

//%%%%%%%%%% RUN PART
runUnary: op=neg phi=runFormula;
runBinary: '(' phi1=runFormula op=rbin phi2=runFormula ')';

// Operators
rbin: rand | ror | rimp | rbimp;
rand: 'AND' | '⋀';
ror: 'OR' | '⋁';
rimp: 'IMP' | '->' | '⇒';
rbimp: 'BIMP' | '<->' | '⇔';

//%%%%%%%%%% flowFormula
flowFormula: (forallFlows | existsFlows) phi=ctl ;

// Operators
forallFlows: 'All' | '𝔸';
existsFlows: 'Exists' | '𝔼';

//%%%%%%%%%% CTL
ctl: tt | ff | atom | ctlUnary | ctlBinary;

atom:  id=ID | id=INT;
ctlUnary: op=unaryOp  phi=ctl;
ctlBinary:      '(' phi1=ctl stdOp=binaryOp phi2=ctl ')' |
             all'(' phi1=ctl op=binaryTempOp phi2=ctl ')' |
          exists'(' phi1=ctl op=binaryTempOp phi2=ctl ')' ;

// Operators
unaryOp: (ex | ax | ef | af | eg | ag | neg);
binaryOp: (and | or | imp | bimp );
binaryTempOp: (until | weak | opRelease);

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
weak: 'W' | '𝓦';
opRelease: 'R' | '𝓡';

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