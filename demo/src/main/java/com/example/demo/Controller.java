package com.example.demo;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rules")
public class Controller {

    private final RuleEngine ruleEngine;
    private Node CombinedRule;

    public Controller() {
        this.ruleEngine = new RuleEngine();
        this.CombinedRule = null;
    }

    @PostMapping("/create")
    public Node createRule(@RequestBody String rule) {
        Node res = ruleEngine.createRule(rule);
        return res;
    }

    @PostMapping("/combine")
    public Node combineRules(@RequestBody List<String> rules) {
        this.CombinedRule = ruleEngine.combineRules(rules);
        return this.CombinedRule;
    }

    @PostMapping("/evaluate")
    public boolean evaluateRule(@RequestBody String userDataJson) {
        JSONObject userData = new JSONObject(userDataJson);
        Map<String, Object> map = userData.toMap();
        return ruleEngine.evaluateAST(this.CombinedRule, map);
    }

}
