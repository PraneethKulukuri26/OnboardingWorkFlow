package com.importflow.validation;

import com.importflow.context.ImportContext;
import com.importflow.context.RecordResult;
import com.importflow.stage.ValidateStage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A composite validator that applies multiple {@link ValidationRule}s
 * to every parsed record in the context.
 *
 * <p>This is the standard way to wire validation into the pipeline.
 * Each rule is applied independently — a single record can accumulate
 * multiple errors from different rules.</p>
 *
 * <p>Failed records are <strong>not removed</strong> from the context.
 * They are flagged with errors so the reporter can display them.</p>
 *
 * @param <T> the domain type being validated
 */
public class CompositeValidator<T> implements ValidateStage<T> {

    private final List<ValidationRule<T>> rules;

    @SafeVarargs
    public CompositeValidator(ValidationRule<T>... rules) {
        this.rules = new ArrayList<>(Arrays.asList(rules));
    }

    public CompositeValidator(List<ValidationRule<T>> rules) {
        this.rules = new ArrayList<>(rules);
    }

    /**
     * Add a rule to this composite validator.
     *
     * @param rule the rule to add
     * @return this validator, for fluent chaining
     */
    public CompositeValidator<T> addRule(ValidationRule<T> rule) {
        rules.add(rule);
        return this;
    }

    @Override
    public void execute(ImportContext<T> context) {
        context.logStage(name(), "Validating " + context.getResults().size()
                + " records with " + rules.size() + " rules");

        for (RecordResult<T> result : context.getResults()) {
            // Skip records already marked as failed (e.g., parse errors)
            if (result.isFailed()) {
                continue;
            }

            T record = result.getParsedObject();
            if (record == null) {
                result.addError("Record was not parsed — cannot validate");
                continue;
            }

            for (ValidationRule<T> rule : rules) {
                ValidationResult vr = rule.validate(record);
                if (!vr.isValid()) {
                    for (String error : vr.getErrors()) {
                        result.addError(error);
                    }
                }
            }
        }

        context.logStage(name(), "Validation complete");
    }

    @Override
    public String name() {
        return "CompositeValidator";
    }
}
