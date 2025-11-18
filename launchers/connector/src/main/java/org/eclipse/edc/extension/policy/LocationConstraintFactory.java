package org.eclipse.edc.extension.policy;

import org.eclipse.edc.connector.controlplane.catalog.spi.policy.CatalogPolicyContext;
import org.eclipse.edc.connector.controlplane.contract.spi.policy.ContractNegotiationPolicyContext;
import org.eclipse.edc.policy.engine.spi.AtomicConstraintRuleFunction;
import org.eclipse.edc.policy.model.Operator;
import org.eclipse.edc.policy.model.Permission;
import org.eclipse.edc.spi.monitor.Monitor;

import java.util.Arrays;
import java.util.Objects;

import static java.lang.String.format;

public class LocationConstraintFactory {
    
    private final Monitor monitor;
    
    public LocationConstraintFactory(Monitor monitor) {
        this.monitor = monitor;
    }

    public AtomicConstraintRuleFunction<Permission, CatalogPolicyContext> generateCatalogFunction() {
        return (operator, rightValue, rule, context) -> {
            monitor.info("\n╔════════════════════════════════════════════╗");
            monitor.info("║   CATALOG COUNTRY POLICY EVALUATION        ║");
            monitor.info("╚════════════════════════════════════════════╝");

            System.err.println("═══════════════════════════════════════════");
            System.err.println("FUNCTION CALLED! FUNCTION CALLED! FUNCTION CALLED!");
            System.err.println("═══════════════════════════════════════════");
            monitor.warning("═══════════════════════════════════════════");
            monitor.warning("CATALOG POLICY FUNCTION WAS CALLED!!!");
            monitor.warning("═══════════════════════════════════════════");
            return evaluateCountryConstraint(operator, rightValue, context.participantAgent().getClaims());
        };
    }

    public AtomicConstraintRuleFunction<Permission, ContractNegotiationPolicyContext> generateNegotiationFunction() {
        return (operator, rightValue, rule, context) -> {
            monitor.info("\n╔════════════════════════════════════════════╗");
            monitor.info("║  NEGOTIATION COUNTRY POLICY EVALUATION     ║");
            monitor.info("╚════════════════════════════════════════════╝");
            
            return evaluateCountryConstraint(operator, rightValue, context.participantAgent().getClaims());
        };
    }

    private boolean evaluateCountryConstraint(Operator operator, Object rightValue, java.util.Map<String, Object> claims) {
        monitor.info("=== POLICY EVALUATION DEBUG ===");
        monitor.info("All claims: " + claims.toString());
        monitor.info("Looking for claim key: 'country'");
        
        var country = claims.get("country");
        
        if (country == null) {
            monitor.severe("❌ ERROR: Country claim is NULL!");
            monitor.severe("Available claim keys: " + claims.keySet());
            return false;
        }
        
        monitor.info(format("Country from token: %s", country));
        monitor.info(format("Required country: %s", rightValue));
        monitor.info(format("Operator: %s", operator));
        
        boolean result = switch (operator) {
            case EQ -> Objects.equals(country.toString(), rightValue.toString());
            case NEQ -> !Objects.equals(country.toString(), rightValue.toString());
            case IN -> Arrays.stream(rightValue.toString().split(","))
                .map(String::trim)
                .anyMatch(v -> Objects.equals(country.toString(), v));
            case IS_NONE_OF -> !Arrays.stream(rightValue.toString().split(","))
                .map(String::trim)
                .anyMatch(v -> Objects.equals(country.toString(), v));
            default -> {
                monitor.warning("❌ Unsupported operator: " + operator);
                yield false;
            }
        };
        
        if (result) {
            monitor.info("✅ POLICY CHECK PASSED");
        } else {
            monitor.severe("❌ POLICY CHECK FAILED - Country " + country + " does not match requirement " + rightValue);
        }
        monitor.info("════════════════════════════════════════════\n");
        
        return result;
    }
}