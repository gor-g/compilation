package fr.usmb.m1isc.compilation.simple;

import static fr.usmb.m1isc.compilation.simple.Sym.*;

import java.util.ArrayList;

public class SyntaxTree {
    private Sym symbol;
    private String symbolString;
    private SyntaxTree expr1, expr2;
    private StringBuilder code = new StringBuilder();
    Counter counter = new Counter();

    public SyntaxTree(Sym symbol, String symbolString, SyntaxTree expr1, SyntaxTree expr2) {
        this.symbol = symbol;
        this.symbolString = symbolString;
        if (expr1 != null) {
            this.expr1 = expr1.setCounter(counter);
        }
        if (expr2 != null) {
            this.expr2 = expr2.setCounter(counter);
        }
    }

    public SyntaxTree(Sym symbol, String symbolString) {
        this.symbol = symbol;
        this.symbolString = symbolString;
    }

    public SyntaxTree() {
    }

    public String getSymbolString() {
        return symbolString;
    }

    public String toString() {
        StringBuilder resultat = new StringBuilder();

        if (expr1 != null || expr2 != null) {
            resultat.append("(").append(symbolString);
        }
        if (expr1 != null) {
            resultat.append(" ").append(expr1.toString());
        }
        if (expr2 != null) {
            resultat.append(" ").append(expr2.toString());
        }
        if (expr1 != null || expr2 != null) {
            resultat.append(")");
        }
        if (expr1 == null && expr2 == null) {
            resultat.append(symbolString);
        }

        return resultat.toString();
    }

    private SyntaxTree append(String s) {
        code.append(s);
        return this;
    }

    private SyntaxTree append(StringBuilder sb) {
        code.append(sb);
        return this;
    }

    private SyntaxTree setCounter(Counter counter) {
        this.counter = counter;
        return this;
    }

    public void generateDataDeclaration(ArrayList<String> dataList) {
        if (expr1 != null) {
            expr1.generateDataDeclaration(dataList);
        }
        if (expr2 != null) {
            expr2.generateDataDeclaration(dataList);
        }
        if (symbol == IDENT && !dataList.contains(symbolString)) {
            dataList.add(symbolString);
        }
    }

    public StringBuilder generateAssemblyCode() {

        counter.increment();

        if (symbol == SEMI) {
            return expr1.generateAssemblyCode().append(expr2.generateAssemblyCode());
        }

        if (symbol == ENTIER || symbol == IDENT) {
            append("\tmov eax, ").append(symbolString).append("\n");
        } else if (symbol == ARITHMETIC) {
            append(expr1.generateAssemblyCode());
            append("\tpush eax\n");
            append(expr2.generateAssemblyCode());
            append("\tpop ebx\n");
            switch (symbolString) {
                case "+":
                    append("\tadd eax, ebx\n");
                    break;
                case "-":
                    append("\tsub ebx, eax\n");
                    append("\tmov eax, ebx\n");
                    break;
                case "*":
                    append("\tmul eax, ebx\n");
                    break;
                case "/":
                    append("\tdiv ebx, eax\n");
                    append("\tmov eax, ebx\n");
                    break;
                default:
                    break;
            }
        } else if (symbol == LET) {
            append(expr2.generateAssemblyCode());
            append("\tmov ");
            append(expr1.symbolString);
            append(", eax\n");
        } else if (symbol == INPUT) {
            append("\tin eax\n");
        } else if (symbol == OUTPUT) {
            append("\tmov eax, ");
            append(expr1.symbolString);
            append("\n");
            append("\tout eax\n");
        } else if (symbol == IF) {
            append(expr1.generateAssemblyCode());
            append("\tjz faux_if_" + counter + "\n");
            append(expr2.expr1.generateAssemblyCode());
            append("\tjmp sortie_if_" + counter + "\n");
            append("faux_if_" + counter + " :\n");
            append(expr2.expr2.generateAssemblyCode());
            append("sortie_if_" + counter + " :\n");
            return code;
        } else if (symbol == WHILE) {
            append("debut_while_" + counter + ":\n");
            append(expr1.generateAssemblyCode());
            append("\tjz sortie_while_" + counter + "\n");
            append(expr2.generateAssemblyCode());
            append("\tjmp debut_while_" + counter + "\n");
            append("sortie_while_" + counter + ":\n");
            return code;
        } else if (symbol == AND) {
            append(expr1.generateAssemblyCode());
            append("\tjz faux_and_" + counter + "\n");
            append(expr2.generateAssemblyCode());
            append("faux_and_" + counter + " :\n");
            return code;
        } else if (symbol == OR) {
            append(expr1.generateAssemblyCode());
            append("\tjnz vrai_or_" + counter + "\n");
            append(expr2.generateAssemblyCode());
            append("\tjnz vrai_or_" + counter + "\n");
            append("vrai_or_" + counter + " :\n");
            return code;
        } else if (symbol == NOT) {
            append(expr1.generateAssemblyCode());
            append("\tjnz faux_not_" + counter + "\n");
            append("\tmov eax, 1\n");
            append("\tjmp sortie_not_" + counter + "\n");
            append("faux_not_" + counter + " :\n");
            append("\tmov eax, 0\n");
            append("sortie_not_" + counter + " :\n");
            return code;
        } else if (symbol == MOD) {
            append(expr2.generateAssemblyCode());
            append("\tpush eax\n");
            append(expr1.generateAssemblyCode());
            append("\tpop ebx\n");
            append("\tmov ecx, eax\n");
            append("\tdiv ecx, ebx\n");
            append("\tmul ecx, ebx\n");
            append("\tsub eax, ecx\n");
            return code;
        } else if (symbol == EGAL) {
            append(expr1.generateAssemblyCode());
            append("\tpush eax\n");
            append(expr2.generateAssemblyCode());
            append("\tpop ebx\n");
            append("\tsub eax, ebx\n");
            append("\tjnz faux_egal_" + counter + "\n");
            append("\tmov eax, 1\n");
            append("\tjmp sortie_egal_" + counter + "\n");
            append("faux_egal_" + counter + " :\n");
            append("\tmov eax, 0\n");
            append("sortie_egal_" + counter + " :\n");
            return code;
        } else if (symbol == GT) {
            append(expr1.generateAssemblyCode());
            append("\tpush eax\n");
            append(expr2.generateAssemblyCode());
            append("\tpop ebx\n");
            append("\tsub eax, ebx\n");
            append("\tjge faux_gt_" + counter + "\n");
            append("\tmov eax, 1\n");
            append("\tjmp sortie_gt_" + counter + "\n");
            append("faux_gt_" + counter + " :\n");
            append("\tmov eax, 0\n");
            append("sortie_gt_" + counter + " :\n");
            return code;
        } else if (symbol == GTE) {
            append(expr1.generateAssemblyCode());
            append("\tpush eax\n");
            append(expr2.generateAssemblyCode());
            append("\tpop ebx\n");
            append("\tsub eax, ebx\n");
            append("\tjg faux_gte_" + counter + "\n");
            append("\tmov eax, 1\n");
            append("\tjmp sortie_gte_" + counter + "\n");
            append("faux_gte_" + counter + " :\n");
            append("\tmov eax, 0\n");
            append("sortie_gte_" + counter + " :\n");
            return code;
        } else if (symbol == LT) {
            append(expr1.generateAssemblyCode());
            append("\tpush eax\n");
            append(expr2.generateAssemblyCode());
            append("\tpop ebx\n");
            append("\tsub eax, ebx\n");
            append("\tjle faux_lt_" + counter + "\n");
            append("\tmov eax, 1\n");
            append("\tjmp sortie_lt_" + counter + "\n");
            append("faux_lt_" + counter + " :\n");
            append("\tmov eax, 0\n");
            append("sortie_lt_" + counter + " :\n");
            return code;
        } else if (symbol == LTE) {
            append(expr1.generateAssemblyCode());
            append("\tpush eax\n");
            append(expr2.generateAssemblyCode());
            append("\tpop ebx\n");
            append("\tsub eax, ebx\n");
            append("\tjl faux_lte_" + counter + "\n");
            append("\tmov eax, 1\n");
            append("\tjmp sortie_lte_" + counter + "\n");
            append("faux_lte_" + counter + " :\n");
            append("\tmov eax, 0\n");
            append("sortie_lte_" + counter + " :\n");
            return code;
        }
        return code;
    }

    public String compile() {
        StringBuilder resultat = new StringBuilder();
        resultat.append("DATA SEGMENT\n");
        ArrayList<String> dataList = new ArrayList<>();
        generateDataDeclaration(dataList);
        for (String data : dataList) {
            resultat.append("\t").append(data).append(" DD\n");
        }
        resultat.append("DATA ENDS\n");
        resultat.append("CODE SEGMENT\n");
        resultat.append(generateAssemblyCode());
        resultat.append("CODE ENDS");
        return resultat.toString();
    }

}
