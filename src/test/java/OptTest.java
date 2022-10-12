import org.jjppp.ast.Program;
import org.jjppp.ir.IRCode;
import org.jjppp.ir.cfg.CFG;
import org.jjppp.ir.cfg.CFGBuilder;
import org.jjppp.tools.optimize.dce.DCE;
import org.jjppp.tools.optimize.lvn.LVN;
import org.jjppp.tools.transform.Transform3AC;
import org.junit.jupiter.api.Test;

public final class OptTest {
    private void run(String filepath) {
        Program program = ParserTest.fromFile(filepath);
        IRCode code = Transform3AC.transform(program);
        for (var fun : code.funList()) {
            CFGBuilder builder = new CFGBuilder(fun);
            CFG cfg = builder.build();
            cfg.toFolder("/home/jjppp/tmp/cfg/");
            cfg.nodes().forEach(x -> x.setBlock(LVN.doLVN(x.block())));
            DCE.doDCE(cfg);
            cfg.toFolder("/home/jjppp/tmp/cfg/opt-");
        }
    }

    @Test
    void testDead() {
        run("simple/dead.sy");
    }

    @Test
    void testMaxflow() {
        run("simple/maxflow.sy");
    }

    @Test
    void testMatrix() {
        run("simple/matrix.sy");
    }

    @Test
    void testRedef() {
        run("simple/redef.sy");
    }
}
