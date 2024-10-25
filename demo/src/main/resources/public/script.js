async function createRule() {
    const ruleString = document.getElementById("ruleString").value;

    const response = await fetch('/api/rules/create', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(ruleString)
    });

    const result = await response.json();
    document.getElementById("result").innerText = JSON.stringify(result, null, 2);
}

async function combineRules() {
    const rulesList = document.getElementById("rulesList").value.split('\n').filter(rule => rule.trim() !== '');

    const response = await fetch('/api/rules/combine', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(rulesList)
    });

    const result = await response.json();
    document.getElementById("result").innerText = JSON.stringify(result, null, 2);
}

document.getElementById('eligibilityForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const formData = new FormData(e.target);
    const data = Object.fromEntries(formData);
    const response = await fetch('/api/rules/evaluate', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(data),
    });
    const result = await response.json();
    document.getElementById('result').innerText = result;
});