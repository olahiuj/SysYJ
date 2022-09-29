package org.jjppp.ast.decl;

import org.jjppp.ast.ASTVisitor;
import org.jjppp.ast.stmt.Block;
import org.jjppp.type.FunType;

import java.util.Collections;
import java.util.List;

public final class FunDecl implements Decl {
    private final String name;
    private final FunType type;
    private final List<Decl> params;
    private Block body;

    public FunDecl(String name, FunType type, List<Decl> params, Block body) {
        this.name = name;
        this.type = type;
        this.params = params;
        this.body = body;
    }

    public static FunDecl of(String name, FunType type, List<Decl> params, Block body) {
        if (params == null) {
            params = Collections.emptyList();
        }
        return new FunDecl(name, type, params, body);
    }

    public Block getBody() {
        return body;
    }

    public void setBody(Block body) {
        this.body = body;
    }

    public List<Decl> getParams() {
        return params;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public FunType type() {
        return type;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visit(this);
    }

    @Override
    public boolean isConst() {
        return type.isConst();
    }
}
