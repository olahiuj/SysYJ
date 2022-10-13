package org.jjppp.ast.exp;

import org.jjppp.ast.ASTVisitor;
import org.jjppp.ast.decl.ArrDecl;
import org.jjppp.runtime.ArrVal;
import org.jjppp.runtime.BaseVal;
import org.jjppp.runtime.Val;
import org.jjppp.tools.symtab.SymTab;
import org.jjppp.type.Type;

import java.util.ArrayList;
import java.util.List;

public record ArrAccExp(ArrDecl arr, List<Exp> indices) implements LVal {
    public static ArrAccExp of(ArrDecl arr, List<Exp> indices) {
        return new ArrAccExp(arr, new ArrayList<>(indices));
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visit(this);
    }

    @Override
    public Val constEval() {
        List<Integer> valIndices = indices.stream()
                .map(Exp::constEval)
                .map(Val::toInt).toList();
        Val val = SymTab.getInstance().getVal(arr.name());
        for (int i = 0; i < arr.type().dim(); ++i) {
            int index = valIndices.get(i);
            val = ((ArrVal) val).exps().get(index);
        }
        assert val instanceof BaseVal;
        return val;
    }

    @Override
    public ArrDecl getDecl() {
        return arr;
    }

    @Override
    public Type type() {
        // TODO: introduction and elimination
        if (indices().size() < arr().type().dim()) {
            return arr.type();
        } else {
            return arr.type().type();
        }
    }
}
