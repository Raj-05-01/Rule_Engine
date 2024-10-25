package com.example.demo;

class Node {
    String type;  // "operator" or "operand"
    Node left;    // left child for operators
    Node right;   // right child for operators
    String value; // Optional value for operand nodes (e.g., condition)

    // Constructor for operator nodes
    public Node(String type, Node left, Node right) {
        this.type = type;
        this.left = left;
        this.right = right;
    }

    // Constructor for operator nodes
    public Node(String type, Node left, Node right, String value) {
        this.type = type;
        this.left = left;
        this.right = right;
        this.value = value;
    }

    // Constructor for operand nodes
    public Node(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Node getLeft() {
        return left;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public Node getRight() {
        return right;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        if ("operand".equals(type)) {
            return String.valueOf(value);
        } else if ("operator".equals(type)) {
            String leftStr = (left != null) ? left.toString() : "";
            String rightStr = (right != null) ? right.toString() : "";
            return "(" + leftStr + " " + value + " " + rightStr + ")";
        }
        return "";
    }

}
