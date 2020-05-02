// For parsing the flow ctl formulas.
// @author Manuel Gieseking
grammar FlowCTLForAllFormat;

flowCTL: runFormula EOF;

// flowCTL
runFormula: ltl | '(' phi1=ltl rimp phi2=runFormula ')' | runBinary | flowFormula;

//%%%%%%%%%% RUN PART
//runUnary: op=neg phi=runFormula;
runBinary: '(' phi1=runFormula op=rbin phi2=runFormula ')';

// Operators
//rbin: rand | ror | rimp | rbimp;
rbin: rand | ror ;
rand: 'AND' | '⋀';
ror: 'OR' | '⋁';
rimp: 'IMP' | '->' | '⇒';
//rbimp: 'BIMP' | '<->' | '⇔';

//%%%%%%%%% LTL
ltl: ltlUnary | ltlBinary | tt | ff | atom;
ltlUnary: op=unaryOp phi=ltl;
ltlBinary:  '(' phi1=ltl op=binaryOp phi2=ltl ')';

// operators
unaryOp: (ltlFinally | ltlGlobally | ltlNext | neg);
binaryOp: (and | or | imp | bimp | until | weak | release );
ltlFinally: 'F' | '◇';
ltlGlobally: 'G' | '⬜';
ltlNext: 'X' | '◯';

//%%%%%%%%%% flowFormula
//flowFormula: op=flowOperators  phi=ctl;
flowFormula: op=forallFlows phi=ctl;

// Operators
//flowOperators: forallFlows | existsFlows;
forallFlows: 'All' | '𝔸';
//existsFlows: 'Exists' | '𝔼';

//%%%%%%%%%% CTL
ctl: tt | ff | atom | ctlUnary | ctlBinary;
ctlUnary: op=ctlUnaryOp  phi=ctl;
ctlBinary:  exists '(' phi1=ctl op=ctlBinaryTempOp phi2=ctl ')' |
               all '(' phi1=ctl op=ctlBinaryTempOp phi2=ctl ')' |
                   '(' phi1=ctl stdOp=ctlBinaryOp phi2=ctl ')';

// Operators
ctlUnaryOp: (ex | ax | ef | af | eg | ag | neg);
ctlBinaryOp: (and | or | imp | bimp );
ctlBinaryTempOp: (until | untilDual | weak | release);

exists: 'E';
all: 'A';

/*exists: 'E';
all: 'A';
ex: exists 'X';
ax: all 'X';
ef: exists 'F';
af: all 'F';
eg: exists 'G';
ag: all 'G';*/

ex: 'EX';
ax: 'AX';
ef: 'EF';
af: 'AF';
eg: 'EG';
ag: 'AG';

// general
neg: 'NEG' | '!' | '¬';
and: 'AND' | '⋏' ;
or: 'OR' | '⋎' ;
imp: 'IMP' | '->' | '→' ;
bimp: 'BIMP' | '<->' | '↔';
until: 'U' | '𝓤';
untilDual: 'U_' | '𝓤_';
weak: 'W' | '𝓦';
release: 'R' | '𝓡';

atom:  id=ID | id=INT;
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