package org.jjppp.tools.parse;

import org.jjppp.ast.Item;
import org.jjppp.ast.decl.ArrDecl;
import org.jjppp.ast.decl.VarDecl;
import org.jjppp.ast.exp.ArrValExp;
import org.jjppp.ast.exp.Exp;
import org.jjppp.ast.stmt.Assign;
import org.jjppp.parser.SysYParser;
import org.jjppp.runtime.BaseVal;
import org.jjppp.runtime.Int;
import org.jjppp.runtime.Val;
import org.jjppp.tools.symtab.SymTab;
import org.jjppp.type.ArrType;
import org.jjppp.type.BaseType;

import java.util.*;
import java.util.stream.Collectors;

public final class LocalDefParser extends DefaultVisitor<List<Item>> {
    private final BaseType type;

    private LocalDefParser(BaseType type) {
        this.type = type;
    }

    public static List<Item> parse(SysYParser.AssContext ctx, BaseType type) {
        return ctx.accept(new LocalDefParser(type));
    }

    private List<Item> parseVarDef(SysYParser.AssContext ctx, Exp defValExp) {
        String name = ctx.ID().getText();
        if (type.isConst()) {
            BaseVal defVal = Optional.ofNullable(defValExp)
                    .map(Exp::constEval)
                    .map(BaseVal.class::cast)
                    .orElse(null);
            VarDecl varDecl = VarDecl.of(name, type, null);
            SymTab.getInstance().addConstVar(varDecl, defVal);
            return Collections.emptyList();
        } else {
            VarDecl varDecl = VarDecl.of(name, type, defValExp);
            SymTab.getInstance().addVar(varDecl, null);
            return List.of(varDecl);
        }
    }

    private List<Item> parseArrDef(SysYParser.AssContext ctx, Exp defValExp) {
        String name = ctx.ID().getText();
        List<Integer> widths = ctx.exp().stream()
                .map(ExpParser::parse)
                .map(Exp::constEval)
                .map(Val::toInt)
                .map(Int::value)
                .collect(Collectors.toList());
        ArrDecl arrDecl = ArrDecl.of(name, ArrType.of(type, widths), (ArrValExp) defValExp, false);
        SymTab.getInstance().addArr(arrDecl);
        List<Item> result = new ArrayList<>(List.of(arrDecl));
        if (!type.isConst() && defValExp != null) {
            result.addAll(Assign.of(arrDecl));
        }
        return result;
    }

    @Override
    public List<Item> visitAss(SysYParser.AssContext ctx) {
        Objects.requireNonNull(ctx.exp());

        Exp defValExp = Optional.ofNullable(ctx.initVal())
                .map(ExpParser::parse)
                .orElse(null);
        if (ctx.exp().isEmpty()) { // var
            return parseVarDef(ctx, defValExp);
        } else { // arr
            return parseArrDef(ctx, defValExp);
        }
    }
}
