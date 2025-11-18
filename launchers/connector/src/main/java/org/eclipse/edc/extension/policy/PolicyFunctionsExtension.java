/*
 *  Copyright (c) 2024 Fraunhofer Institute for Software and Systems Engineering
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Fraunhofer Institute for Software and Systems Engineering - initial API and implementation
 *
 */

package org.eclipse.edc.extension.policy;

import org.eclipse.edc.connector.controlplane.catalog.spi.policy.CatalogPolicyContext;
import org.eclipse.edc.connector.controlplane.contract.spi.policy.ContractNegotiationPolicyContext;
import org.eclipse.edc.policy.engine.spi.PolicyEngine;
import org.eclipse.edc.policy.engine.spi.RuleBindingRegistry;
import org.eclipse.edc.policy.model.Permission;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;

import static org.eclipse.edc.connector.controlplane.catalog.spi.policy.CatalogPolicyContext.CATALOG_SCOPE;
import static org.eclipse.edc.connector.controlplane.contract.spi.policy.ContractNegotiationPolicyContext.NEGOTIATION_SCOPE;
import static org.eclipse.edc.jsonld.spi.PropertyAndTypeNames.ODRL_USE_ACTION_ATTRIBUTE;
import static org.eclipse.edc.policy.engine.spi.PolicyEngine.ALL_SCOPES;
import static org.eclipse.edc.spi.constants.CoreConstants.EDC_NAMESPACE;

public class PolicyFunctionsExtension implements ServiceExtension {
    private static final String LOCATION_CONSTRAINT_KEY =  "country";
    private static final String PARTICIPANT_CONSTRAINT_KEY = "participantId";
    private static final String KEY_POLICY_EVALUATION_TIME = EDC_NAMESPACE + "policyEvaluationTime";

    
    
    @Inject
    private RuleBindingRegistry ruleBindingRegistry;
    @Inject
    private PolicyEngine policyEngine;
    
    @Override
    public String name() {
        return "Sample policy functions";
    }
    
    @Override
    public void initialize(ServiceExtensionContext context) {
        System.out.println(EDC_NAMESPACE+ ": EDC NAMESPACE VALUE");
        var monitor = context.getMonitor();
        System.out.println("╔════════════════════════════════════════════╗");
        System.out.println("║  POLICY FUNCTIONS EXTENSION INITIALIZING  ║");
        System.out.println("╔════════════════════════════════════════════╗");
        

        ruleBindingRegistry.bind(ODRL_USE_ACTION_ATTRIBUTE, ALL_SCOPES);

        ruleBindingRegistry.bind(LOCATION_CONSTRAINT_KEY, CATALOG_SCOPE);
        ruleBindingRegistry.bind(LOCATION_CONSTRAINT_KEY, NEGOTIATION_SCOPE);

        // ruleBindingRegistry.bind(PARTICIPANT_CONSTRAINT_KEY, CATALOG_SCOPE);
        // ruleBindingRegistry.bind(PARTICIPANT_CONSTRAINT_KEY, NEGOTIATION_SCOPE);

        // ruleBindingRegistry.bind(KEY_POLICY_EVALUATION_TIME, CATALOG_SCOPE);
        // ruleBindingRegistry.bind(KEY_POLICY_EVALUATION_TIME, NEGOTIATION_SCOPE);

        monitor.warning("Constraint key being registered: '" + LOCATION_CONSTRAINT_KEY + "'");
        monitor.warning("CATALOG_SCOPE value: '" + CATALOG_SCOPE + "'");
        monitor.warning("NEGOTIATION_SCOPE value: '" + NEGOTIATION_SCOPE + "'");
        LocationConstraintFactory locationConstraint = new LocationConstraintFactory(monitor);
        policyEngine.registerFunction(CatalogPolicyContext.class, Permission.class, LOCATION_CONSTRAINT_KEY, locationConstraint.generateCatalogFunction());
        policyEngine.registerFunction(ContractNegotiationPolicyContext.class, Permission.class, LOCATION_CONSTRAINT_KEY, locationConstraint.generateNegotiationFunction());
        System.out.println("✓ Location constraint registered for: " + LOCATION_CONSTRAINT_KEY);

        ParticipantConstraintFactory participantConstraint = new ParticipantConstraintFactory(monitor);
        policyEngine.registerFunction(CatalogPolicyContext.class, Permission.class, PARTICIPANT_CONSTRAINT_KEY, participantConstraint.generateRuleFunction());
        policyEngine.registerFunction(ContractNegotiationPolicyContext.class, Permission.class, PARTICIPANT_CONSTRAINT_KEY, participantConstraint.generateRuleFunction());
        System.out.println("✓ Participant constraint registered for: " + PARTICIPANT_CONSTRAINT_KEY);

        // TimeConstraintFactory timeConstraint = new TimeConstraintFactory(monitor);
        // policyEngine.registerFunction(CatalogPolicyContext.class, Permission.class, KEY_POLICY_EVALUATION_TIME, timeConstraint.generateRuleFunction());
        // policyEngine.registerFunction(ContractNegotiationPolicyContext.class, Permission.class, KEY_POLICY_EVALUATION_TIME, timeConstraint.generateRuleFunction());
        // System.out.println("✓ Time constraint registered for: " + KEY_POLICY_EVALUATION_TIME);
        System.out.println("╚════════════════════════════════════════════╝");
        monitor.info("Policy functions extension initialized successfully");
    }
}