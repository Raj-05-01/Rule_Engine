package com.example.demo;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RuleEngine {

    private List<String> tokens;
    private int currentTokenIndex;

    public Node createRule(String ruleString) {
        tokens = tokenize(ruleString);
        currentTokenIndex = 0;
        return parseExpression();
    }

    private List<String> tokenize(String ruleString) {
        // Regex to match conditions, operators, and parentheses
        Pattern pattern = Pattern.compile("\\(|\\)|\\s+|AND|OR|[!=<>]+|\\w+|'[^']*'");
        Matcher matcher = pattern.matcher(ruleString);
        List<String> tokens = new ArrayList<>();

        while (matcher.find()) {
            String token = matcher.group().trim();
            if (!token.isEmpty()) {
                tokens.add(token);
            }
        }
        return tokens;
    }

    private Node parseExpression() {
        Node leftNode = parseFactor();
        while (currentTokenIndex < tokens.size() && (tokens.get(currentTokenIndex).equalsIgnoreCase("OR") || tokens.get(currentTokenIndex).equalsIgnoreCase("AND"))) {
            String tokenValue = tokens.get(currentTokenIndex);
            currentTokenIndex++; // Skip "OR"
            Node rightNode = parseFactor();
            leftNode = new Node("operator", leftNode, rightNode);
            leftNode.value = tokenValue; // Set operator value
        }

        return leftNode;
    }


    private Node parseFactor() {
        String token = tokens.get(currentTokenIndex);

        if (token.equals("(")) {
            currentTokenIndex++; // Skip "("
            Node node = parseExpression();
            currentTokenIndex++; // Skip ")"
            return node;
        } else {
            return parseCondition();
        }
    }

    private Node parseCondition() {
        String attribute = tokens.get(currentTokenIndex++);
        String operator = tokens.get(currentTokenIndex++);
        String value = tokens.get(currentTokenIndex++);

        // Return a node for the condition
        return new Node("operand", attribute + " " + operator + " " + value);
    }


    public Node combineRules(List<String> rules) {
        // If there are no rules, return null
        if (rules == null || rules.isEmpty()) {
            return null;
        }

        // Create a list to hold individual ASTs
        List<Node> asts = new ArrayList<>();
        for (String rule : rules) {
            Node ast = createRule(rule);
            if (ast != null) {
                asts.add(ast);
            }
        }

        // Combine the ASTs using the "OR" operator as the root for simplicity
        // You can adjust this based on the operator frequency or other heuristics
        Node combinedAst = null;

        for (Node ast : asts) {
            if (combinedAst == null) {
                combinedAst = ast; // Initialize the combined AST
            } else {
                // Create an "OR" operator node to combine existing and new ASTs
                combinedAst = new Node("operator", combinedAst, ast, "OR");
            }
        }

        return combinedAst; // Return the root of the combined AST
    }


    public boolean evaluateAST(Node ast, Map<String, Object> data) {
        if (ast == null) {
            return false; // Handle null AST
        }

        // Evaluate based on the type of node
        if ("operand".equals(ast.getType())) {
            return evaluateOperand(ast, data);
        } else if ("operator".equals(ast.getType())) {
            boolean leftEval = evaluateAST(ast.getLeft(), data);
            boolean rightEval = evaluateAST(ast.getRight(), data);
            return evaluateOperator(ast.getValue().toString(), leftEval, rightEval);
        }

        return false; // In case of unexpected node type
    }

    private boolean evaluateOperand(Node node, Map<String, Object> data) {
        String expression = node.getValue().toString(); // e.g., "age > 30"

        // Split the expression into parts (e.g., ["age", ">", "30"])
        String[] parts = expression.split(" ");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid operand format: " + expression);
        }

        String attribute = parts[0];    // e.g., "age"
        String operator = parts[1];      // e.g., ">"
        String valueStr = parts[2];      // e.g., "30"
        Object comparisonValue;

        // Convert the comparison value to the appropriate type
        if (valueStr.startsWith("'") && valueStr.endsWith("'")) {
            // Handle string values (remove quotes)
            comparisonValue = valueStr.substring(1, valueStr.length() - 1);
        } else {
            // Handle numeric values
            comparisonValue = Integer.parseInt(valueStr);
        }

        // Check if the attribute exists in the data
        if (!data.containsKey(attribute)) {
            return false; // Attribute not found
        }

        // Get the user's attribute value
        Object userValue = data.get(attribute);
        if (comparisonValue instanceof Integer) {
            userValue = Integer.parseInt(userValue.toString());
        }


        // Compare values based on the operator
        switch (operator) {
            case ">":
                return (Integer) userValue > (Integer) comparisonValue;
            case "<":
                return (Integer) userValue < (Integer) comparisonValue;
            case "=":
                return userValue.equals(comparisonValue);
            case "!=":
                return !userValue.equals(comparisonValue);
            // Add more operators as needed (e.g., >=, <=)
            default:
                throw new IllegalArgumentException("Unsupported operator: " + operator);
        }
    }

    public boolean evaluateOperator(String operator, boolean leftEval, boolean rightEval) {
        if ("AND".equals(operator)) {
            return leftEval && rightEval;
        } else if ("OR".equals(operator)) {
            return leftEval || rightEval;
        }
        return false; // Unsupported operator
    }


}
