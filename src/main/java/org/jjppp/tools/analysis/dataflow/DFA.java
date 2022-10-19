package org.jjppp.tools.analysis.dataflow;

import org.jjppp.ir.cfg.CFG;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public abstract class DFA<T extends AbsData<T>> {
    protected final Map<CFG.Node, T> inMap = new HashMap<>();
    protected final Map<CFG.Node, T> outMap = new HashMap<>();
    protected final CFG cfg;
    protected final Queue<CFG.Node> queue = new LinkedList<>();

    public DFA(CFG cfg) {
        this.cfg = cfg;
    }

    protected abstract void init();

    protected abstract T transfer(CFG.Node node, T dataIn);

    protected Map<CFG.Node, T> solve() {
        queue.add(cfg.entry());
        while (!queue.isEmpty()) {
            CFG.Node node = queue.poll();

            if (!node.equals(cfg.entry())) {
                T dataIn = node.getPred().stream()
                        .map(outMap::get)
                        .reduce(inMap.get(node), AbsData::merge);
                inMap.put(node, dataIn);
            }

            T dataOut = outMap.get(node);
            T newDataOut = transfer(node, inMap.get(node));

            if (!dataOut.equals(newDataOut)) {
                outMap.put(node, newDataOut);
                queue.addAll(node.getSucc());
            }
        }
        return inMap;
    }
}
