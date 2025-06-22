package org.Handlers.Logic;

/**
 * Defines analyzing input data of type {@code I}
 * and outputting result of type {@code O}.
 *
 * @param <I> the type of input to be analyzed
 * @param <O> the analysis result
 */
public interface Analyzer<I, O> {
    /**
     * Analyzes the input and creates an output.
     * @param input of data type {@code I}
     * @return resulting output of data type {@code O}
     */
    O analyze(I input);
}
