class Calculator {
    constructor() {
        this.previousOperand = '';
        this.currentOperand = '0';
        this.operation = undefined;
        this.history = [];
        this.waitingForOperand = false;
        this.init();
    }

    init() {
        this.updateDisplay();
        this.attachEventListeners();
        this.loadHistory();
    }

    attachEventListeners() {
        // Number buttons
        document.querySelectorAll('[data-number]').forEach(button => {
            button.addEventListener('click', () => {
                this.appendNumber(button.getAttribute('data-number'));
                this.updateDisplay();
                this.addRippleEffect(button);
            });
        });

        // Operation buttons
        document.querySelectorAll('[data-action]').forEach(button => {
            button.addEventListener('click', () => {
                const action = button.getAttribute('data-action');
                this.handleAction(action);
                this.updateDisplay();
                this.addRippleEffect(button);
            });
        });
    }

    appendNumber(number) {
        if (this.waitingForOperand) {
            this.currentOperand = number;
            this.waitingForOperand = false;
        } else {
            // Handle double zero
            if (number === '00') {
                if (this.currentOperand === '0') {
                    this.currentOperand = '0';
                } else {
                    this.currentOperand += '00';
                }
                return;
            }

            // Prevent multiple decimals
            if (number === '.' && this.currentOperand.includes('.')) return;

            // Replace if current operand is '0' and not decimal
            if (this.currentOperand === '0' && number !== '.') {
                this.currentOperand = number;
            } else {
                this.currentOperand += number;
            }
        }
        this.updateDisplay();
    }

    clear() {
        this.currentOperand = '0';
        this.previousOperand = '';
        this.operation = undefined;
        this.waitingForOperand = false;
    }

    clearEntry() {
        this.currentOperand = '0';
    }

    delete() {
        if (this.currentOperand.length === 1 || this.currentOperand === '0') {
            this.currentOperand = '0';
        } else {
            this.currentOperand = this.currentOperand.slice(0, -1);
        }
    }

    chooseOperation(operation) {
        if (this.currentOperand === '') return;

        if (this.previousOperand !== '' && !this.waitingForOperand) {
            this.compute();
        }

        this.operation = operation;
        this.previousOperand = this.currentOperand;
        this.waitingForOperand = true;
    }

    async compute() {
        if (this.previousOperand === '' || this.currentOperand === '' || !this.operation) return;

        const num1 = parseFloat(this.previousOperand);
        const num2 = parseFloat(this.currentOperand);

        if (isNaN(num1) || isNaN(num2)) return;

        const operationSymbols = {
            'add': '+',
            'subtract': '-',
            'multiply': '×',
            'divide': '÷',
            'percent': '% of',
            'modulo': 'mod'
        };

        // For percentage operation - special handling
        if (this.operation === 'percent') {
            const result = (num1 * num2) / 100;
            const expression = `${num1} ${operationSymbols[this.operation]} ${num2}`;
            this.addToHistory(expression, result);
            this.currentOperand = result.toString();
            this.operation = undefined;
            this.previousOperand = '';
            this.waitingForOperand = false;
            this.updateDisplay();
            return;
        }

        // For other operations, call backend API
        try {
            const response = await fetch('/api/calculate', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    num1: num1,
                    num2: num2,
                    operation: this.operation
                })
            });

            const data = await response.json();

            if (data.success) {
                this.addToHistory(data.expression || `${num1} ${operationSymbols[this.operation]} ${num2}`, data.result);
                this.currentOperand = data.result.toString();
                this.operation = undefined;
                this.previousOperand = '';
                this.waitingForOperand = false;
            } else {
                this.showError(data.error);
                this.currentOperand = 'Error';
                setTimeout(() => {
                    this.clear();
                    this.updateDisplay();
                }, 1500);
            }
        } catch (error) {
            this.showError('Calculation error: ' + error.message);
            this.currentOperand = 'Error';
            setTimeout(() => {
                this.clear();
                this.updateDisplay();
            }, 1500);
        }

        this.updateDisplay();
    }

    calculatePercentage() {
        if (this.currentOperand === '') return;

        const num = parseFloat(this.currentOperand);
        if (isNaN(num)) return;

        const result = num / 100;
        this.currentOperand = result.toString();
        this.updateDisplay();
    }

    addToHistory(expression, result) {
        const historyItem = {
            expression: expression,
            result: result,
            timestamp: new Date().toLocaleTimeString()
        };

        this.history.unshift(historyItem);

        // Keep only last 15 items
        if (this.history.length > 15) {
            this.history.pop();
        }

        this.saveHistory();
        this.renderHistory();
    }

    renderHistory() {
        const historyList = document.getElementById('historyList');
        if (!historyList) return;

        if (this.history.length === 0) {
            historyList.innerHTML = '<div style="text-align: center; color: #999;">No calculations yet</div>';
            return;
        }

        historyList.innerHTML = this.history.map((item, index) => `
            <div class="history-item">
                <div>
                    <span class="history-expression">${item.expression}</span>
                    <span class="history-result"> = ${this.formatNumber(item.result)}</span>
                </div>
                <div>
                    <span class="history-time">${item.timestamp}</span>
                    <button class="history-clear" onclick="calculator.deleteHistoryItem(${index})">×</button>
                </div>
            </div>
        `).join('');
    }

    deleteHistoryItem(index) {
        this.history.splice(index, 1);
        this.saveHistory();
        this.renderHistory();
    }

    clearHistory() {
        this.history = [];
        this.saveHistory();
        this.renderHistory();
        this.showError('History cleared!', 'success');
    }

    saveHistory() {
        localStorage.setItem('calculatorHistory', JSON.stringify(this.history));
    }

    loadHistory() {
        const savedHistory = localStorage.getItem('calculatorHistory');
        if (savedHistory) {
            this.history = JSON.parse(savedHistory);
            this.renderHistory();
        }
    }

    handleAction(action) {
        switch(action) {
            case 'clear':
                this.clear();
                break;
            case 'clear-entry':
                this.clearEntry();
                break;
            case 'delete':
                this.delete();
                break;
            case 'add':
            case 'subtract':
            case 'multiply':
            case 'divide':
            case 'percent':
            case 'modulo':
                this.chooseOperation(action);
                break;
            case 'equals':
                this.compute();
                break;
            case 'decimal':
                this.appendNumber('.');
                break;
        }
        this.updateDisplay();
    }

    updateDisplay() {
        const currentOperandElement = document.getElementById('currentOperand');
        const previousOperandElement = document.getElementById('previousOperand');

        if (currentOperandElement) {
            currentOperandElement.textContent = this.formatNumber(this.currentOperand);
        }

        if (previousOperandElement) {
            const operationSymbols = {
                'add': '+',
                'subtract': '-',
                'multiply': '×',
                'divide': '÷',
                'percent': '% of',
                'modulo': 'mod'
            };

            if (this.operation && this.previousOperand) {
                previousOperandElement.textContent =
                    `${this.formatNumber(this.previousOperand)} ${operationSymbols[this.operation] || this.operation}`;
            } else {
                previousOperandElement.textContent = '';
            }
        }
    }

    formatNumber(number) {
        if (number === 'Error') return 'Error';
        if (number === '' || number === undefined) return '0';

        const num = parseFloat(number);
        if (isNaN(num)) return '0';

        // Format with commas for large numbers
        if (Math.abs(num) > 999999) {
            return num.toExponential(4);
        }

        // Limit decimal places
        if (num % 1 !== 0) {
            // Remove trailing zeros
            let formatted = num.toFixed(8).replace(/\.?0+$/, '');
            return formatted;
        }

        return num.toString();
    }

    showError(message, type = 'error') {
        const toast = document.createElement('div');
        toast.className = `error-toast ${type}`;
        toast.textContent = message;
        toast.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            background: ${type === 'error' ? '#f44336' : '#4caf50'};
            color: white;
            padding: 12px 20px;
            border-radius: 8px;
            z-index: 1000;
            animation: slideInRight 0.3s ease;
            box-shadow: 0 4px 12px rgba(0,0,0,0.2);
        `;

        document.body.appendChild(toast);

        setTimeout(() => {
            toast.remove();
        }, 3000);
    }

    addRippleEffect(button) {
        button.style.transform = 'scale(0.95)';
        setTimeout(() => {
            button.style.transform = 'scale(1)';
        }, 100);
    }
}

// Initialize calculator when DOM is loaded
let calculator;
document.addEventListener('DOMContentLoaded', () => {
    calculator = new Calculator();

    // Add keyboard support
    document.addEventListener('keydown', (e) => {
        const key = e.key;

        // Numbers
        if (/[0-9]/.test(key)) {
            calculator.appendNumber(key);
            calculator.updateDisplay();
        }
        // Decimal point
        else if (key === '.') {
            calculator.appendNumber('.');
            calculator.updateDisplay();
        }
        // Operations
        else if (key === '+') {
            calculator.chooseOperation('add');
            calculator.updateDisplay();
        }
        else if (key === '-') {
            calculator.chooseOperation('subtract');
            calculator.updateDisplay();
        }
        else if (key === '*') {
            calculator.chooseOperation('multiply');
            calculator.updateDisplay();
        }
        else if (key === '/') {
            e.preventDefault();
            calculator.chooseOperation('divide');
            calculator.updateDisplay();
        }
        else if (key === '%') {
            calculator.chooseOperation('percent');
            calculator.updateDisplay();
        }
        // Enter key for equals
        else if (key === 'Enter' || key === '=') {
            calculator.compute();
            calculator.updateDisplay();
        }
        // Escape key for clear
        else if (key === 'Escape') {
            calculator.clear();
            calculator.updateDisplay();
        }
        // Backspace for delete
        else if (key === 'Backspace') {
            calculator.delete();
            calculator.updateDisplay();
        }
    });
});

// Export for global access
window.calculator = calculator;