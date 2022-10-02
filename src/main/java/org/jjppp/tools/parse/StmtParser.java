package org.jjppp.tools.parse;

import org.jjppp.ast.Item;
import org.jjppp.ast.stmt.*;
import org.jjppp.parser.SysYParser;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class StmtParser extends DefaultVisitor<Stmt> {
    private final static StmtParser INSTANCE = new StmtParser();

    private StmtParser() {
    }

    public static Stmt parse(SysYParser.StmtContext ctx) {
        return ctx.accept(INSTANCE);
    }

    @Override
    public Stmt visitReturnStmt(SysYParser.ReturnStmtContext ctx) {
        return Return.of(
                Optional.ofNullable(ctx.exp())
                        .map(ExpParser::parse)
                        .orElse(null));
    }

    @Override
    public Stmt visitAssignStmt(SysYParser.AssignStmtContext ctx) {
        return Assign.of(LValParser.parse(ctx.lVal()), ExpParser.parse(ctx.exp()));
    }

    @Override
    public Stmt visitContinStmt(SysYParser.ContinStmtContext ctx) {
        return Continue.getInstance();
    }

    @Override
    public Stmt visitBreakStmt(SysYParser.BreakStmtContext ctx) {
        return Break.getInstance();
    }

    @Override
    public Stmt visitExpStmt(SysYParser.ExpStmtContext ctx) {
        return ExpStmt.of(ExpParser.parse(ctx.exp()));
    }

    @Override
    public Stmt visitEmptyStmt(SysYParser.EmptyStmtContext ctx) {
        return Empty.getInstance();
    }

    @Override
    public Stmt visitIftStmt(SysYParser.IftStmtContext ctx) {
        return If.of(
                CondParser.parse(ctx.cond()),
                StmtParser.parse(ctx.stmt()));
    }

    @Override
    public Stmt visitIfteStmt(SysYParser.IfteStmtContext ctx) {
        return Ife.of(
                CondParser.parse(ctx.cond()),
                StmtParser.parse(ctx.sTru),
                StmtParser.parse(ctx.sFls));
    }

    @Override
    public Stmt visitWhileStmt(SysYParser.WhileStmtContext ctx) {
        return While.of(
                CondParser.parse(ctx.cond()),
                StmtParser.parse(ctx.stmt()));
    }

    @Override
    public Block visitBlockStmt(SysYParser.BlockStmtContext ctx) {
        List<Item> items = ctx.block().blockItem().stream()
                .map(ItemParser::parse)
                .flatMap(Collection::stream).collect(Collectors.toList());
        return Block.of(items);
    }
}
