package org.jjppp.tools.parse;

import org.jjppp.ast.exp.BinExp;
import org.jjppp.ast.exp.Exp;
import org.jjppp.ast.exp.OpExp;
import org.jjppp.ast.exp.UnExp;
import org.jjppp.parser.SysYParser;

public class CondParser extends DefaultVisitor<Exp> {
    private final static CondParser INSTANCE = new CondParser();

    private CondParser() {
    }

    public static Exp parse(SysYParser.CondContext ctx) {
        return ctx.accept(INSTANCE);
    }

    @Override
    public Exp visitEqCond(SysYParser.EqCondContext ctx) {
        return BinExp.of(
                switch (ctx.op.getText().charAt(0)) {
                    case '=' -> OpExp.BiOp.EQ;
                    case '!' -> OpExp.BiOp.NE;
                    default -> throw new ParserException("unknown op");
                },
                ExpParser.parse(ctx.lhs),
                ExpParser.parse(ctx.rhs));
    }

    @Override
    public Exp visitRawCond(SysYParser.RawCondContext ctx) {
        if (ctx.op == null) {
            return UnExp.of(OpExp.UnOp.NZ, ExpParser.parse(ctx.exp()));
        } else {
            return UnExp.of(OpExp.UnOp.IZ, ExpParser.parse(ctx.exp()));
        }
    }

    @Override
    public Exp visitUnaryCond(SysYParser.UnaryCondContext ctx) {
        if (ctx.op.getText().charAt(0) == '!') {
            return UnExp.of(OpExp.UnOp.NOT, CondParser.parse(ctx.cond()));
        }
        throw new ParserException("unknown op");
    }

    @Override
    public Exp visitRelCond(SysYParser.RelCondContext ctx) {
        Exp lhs = ExpParser.parse(ctx.lhs);
        Exp rhs = ExpParser.parse(ctx.rhs);

        if (ctx.op.getText().equals("<")) {
            return BinExp.of(OpExp.BiOp.LT, lhs, rhs);
        } else if (ctx.op.getText().equals("<=")) {
            return BinExp.of(OpExp.BiOp.LE, lhs, rhs);
        } else if (ctx.op.getText().equals(">")) {
            return BinExp.of(OpExp.BiOp.GT, lhs, rhs);
        } else if (ctx.op.getText().equals(">=")) {
            return BinExp.of(OpExp.BiOp.GE, lhs, rhs);
        }
        throw new ParserException("unknown op");
    }

    @Override
    public Exp visitBinaryCond(SysYParser.BinaryCondContext ctx) {
        return BinExp.of(
                switch (ctx.op.getText().charAt(0)) {
                    case '&' -> OpExp.BiOp.AND;
                    case '|' -> OpExp.BiOp.OR;
                    default -> throw new ParserException("unknown op");
                },
                CondParser.parse(ctx.lhs),
                CondParser.parse(ctx.rhs));
    }
}
