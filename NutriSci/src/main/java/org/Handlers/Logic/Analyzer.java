package org.Handlers.Logic;

public interface Analyzer<I, O> {
    O analyze(I input);
}
